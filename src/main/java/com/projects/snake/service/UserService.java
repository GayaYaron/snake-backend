package com.projects.snake.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projects.snake.exception.AlreadyPurchasedException;
import com.projects.snake.exception.NoColorException;
import com.projects.snake.exception.NoDefaultDesignException;
import com.projects.snake.exception.NotEnoughCoinsException;
import com.projects.snake.exception.NotFoundException;
import com.projects.snake.exception.util.NullUtil;
import com.projects.snake.model.ColorPack;
import com.projects.snake.model.ColorType;
import com.projects.snake.model.Design;
import com.projects.snake.model.User;
import com.projects.snake.model.UserColor;
import com.projects.snake.repository.ColorPackRepo;
import com.projects.snake.repository.DesignRepo;
import com.projects.snake.repository.UserColorRepo;
import com.projects.snake.repository.UserRepo;
import com.projects.snake.service.detail.UserDetail;
import com.projects.snake.service.response.LoginResponse;
import com.projects.snake.service.response.LoginResponseMaker;

@Service
@Transactional
public class UserService {
	@Autowired
	private DesignRepo designRepo;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private ColorPackRepo colorRepo;
	@Autowired
	private UserColorRepo userColorRepo;
	@Autowired
	private LoginResponseMaker responseMaker;
	@Autowired
	private NullUtil nullUtil;
	@Autowired
	private UserDetail detail;

	public Optional<LoginResponse> login(String nickname, String password) {
		nullUtil.check(nickname, "nickname");
		nullUtil.check(password, "password");
		Optional<User> optionalUser = userRepo.findByNicknameAndPassword(nickname, password);
		if (optionalUser.isEmpty()) {
			return Optional.empty();
		}
		User user = optionalUser.get();
		return Optional.of(responseMaker.make(user.getId(), user.getNickname(), user.getCoins()));
	}

	/**
	 * sets the chosen design of the user if the user has such design
	 * 
	 * @param designId
	 * @throws NotFoundException- if such design does not exist in the user's list
	 */
	@Transactional(readOnly = false)
	public void setChosenDesign(Integer designId) {
		if (!(designId == null)) {
			if (!(designRepo.existsByIdUserId(designId, detail.getId()))) {
				throw new NotFoundException("design");
			}
			User user = getUser().get();
			user.setChosenDesign(designId);
			userRepo.save(user);
		}
	}

	/**
	 * 
	 * @return Optional<User> the details of the user if such exist
	 */
	public Optional<User> getUser() {
		return userRepo.findById(detail.getId());
	}

	/**
	 * 
	 * @param designId
	 * @return an optional of the design if such exists
	 */
	public Optional<Design> getDesign(Integer designId) {
		return (designId != null) ? designRepo.findById(designId) : Optional.empty();
	}

	/**
	 * buys the colour pack
	 * 
	 * @param colorId
	 * @throws AlreadyPurchasedException - if the user already has this colour pack
	 * @throws NotFoundException         - if such colour pack was not found
	 * @throws NotEnoughCoinsException   - if the user does not have enough coins to
	 *                                   buy the colour pack
	 */
	public void buyColorPack(Integer colorId) {
		if (colorId != null) {
			if (userColorRepo.existsByUserIdAndColorPackId(detail.getId(), colorId)) {
				throw new AlreadyPurchasedException("color pack");
			}
			Optional<ColorPack> optional = colorRepo.findById(colorId);
			if (optional.isEmpty()) {
				throw new NotFoundException("color pack");
			}
			ColorPack colorPack = optional.get();
			User user = getUser().get();
			if (colorPack.getPrice() > user.getCoins()) {
				throw new NotEnoughCoinsException("color pack");
			}
			userColorRepo.save(new UserColor(user, colorPack));
			user.setCoins(user.getCoins() - colorPack.getPrice());
			userRepo.save(user);
		}
	}

	/**
	 * saves the design, saves the design in the user list sets the design to be the
	 * user's chosen design
	 * 
	 * @param design
	 * @throws NoColorException - if the user does not own one or more of the
	 *                          colours for the design
	 */
	@Transactional(readOnly = false)
	public void addDesign(Design design) {
		if (design != null) {
			String snakeCol = design.getSnakeColor();
			String borderCol = design.getBorderColor();
			String foodCol = design.getFoodColor();
			checkColorsInDesign(snakeCol, borderCol, foodCol);
			User user = userRepo.findById(detail.getId()).get();
			design.setUser(user);
			design.setId(null);
			Design savedDesign = designRepo.save(design);
			user.setChosenDesign(savedDesign.getId());
			userRepo.save(user);
		}
	}

	private void checkColorsInDesign(String snakeCol, String borderCol, String foodCol) {
		List<UserColor> userColors = userColorRepo.findByUserId(detail.getId());
		boolean snake = false;
		boolean border = false;
		boolean food = false;
		for (int i = 0; i < userColors.size() && (!snake || !border || !food); i++) {
			ColorPack colorPack = userColors.get(i).getColorPack();
			ColorType type = colorPack.getType();
			switch (type) {
			case SNAKE:
				snake = (snake || hasColor(snake, colorPack, snakeCol));
				break;
			case BORDER:
				border = (border || hasColor(border, colorPack, borderCol));
				break;

			default:
				food = (food || hasColor(food, colorPack, foodCol));
				break;
			}
		}
		if (!snake) {
			throw new NoColorException("snake");
		}
		if (!border) {
			throw new NoColorException("border");
		}
		if (!food) {
			throw new NoColorException("food");
		}
	}

	private boolean hasColor(boolean found, ColorPack colorPack, String searchColor) {
		if (!found) {
			List<String> colors = colorPack.getColors();
			found = (found || colors.contains(searchColor));
		}
		return found;
	}

	/**
	 * 
	 * @return the chosen design if such exists, otherwise, user's default design
	 * @throws NoDefaultDesignException - if no chosen design nor default design was
	 *                                  found
	 */
	public Design getChosenDesign() {
		 Integer chosenDesign = getUser().get().getChosenDesign();
		 Optional<Design> optionalChosen = designRepo.findById(chosenDesign);
		 if(optionalChosen.isPresent()) {
			 return optionalChosen.get();
		 }
		 Optional<Design> optionalDefault = designRepo.findFirstByNameAndUserId("default", detail.getId());
		 if(optionalDefault.isEmpty()) {
			 throw new NoDefaultDesignException();
		 }
		 return optionalDefault.get();
	}

	/**
	 * deletes the design as long as it is not called default or that the user has another default design
	 * @param designId
	 * @throws NoDefaultDesignException - if it is the only default design the user has
	 */
	public void deleteDesign(Integer designId) {
		if (designId != null) {
			Optional<Design> optionalDesign = designRepo.findByIdAndUserId(designId, detail.getId());
			if (optionalDesign.isPresent()) {
				Design design = optionalDesign.get();
				if (design.getName() == "default"
						&& designRepo.findByNameAndUserId("default", detail.getId()).size() <= 1) {
					throw new NoDefaultDesignException();
				}
				designRepo.delete(design);
			}
		}
	}
}
