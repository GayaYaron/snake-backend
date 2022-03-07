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
import com.projects.snake.model.DesignBase;
import com.projects.snake.model.User;
import com.projects.snake.model.UserColor;
import com.projects.snake.model.UserDesign;
import com.projects.snake.repository.ColorPackRepo;
import com.projects.snake.repository.DesignBaseRepo;
import com.projects.snake.repository.DesignRepo;
import com.projects.snake.repository.UserColorRepo;
import com.projects.snake.repository.UserDesignRepo;
import com.projects.snake.repository.UserRepo;
import com.projects.snake.service.detail.UserDetail;
import com.projects.snake.service.response.LoginResponse;
import com.projects.snake.service.response.LoginResponseMaker;

@Service
@Transactional
public class UserService {
	@Autowired
	private DesignBaseRepo designBaseRepo;
	@Autowired
	private DesignRepo designRepo;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private UserDesignRepo userDesignRepo;
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
			if (!(userDesignRepo.existsByUserIdAndDesignId(detail.getId(), designId))) {
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
			User user = userRepo.getById(detail.getId());
			if (colorPack.getPrice() > user.getCoins()) {
				throw new NotEnoughCoinsException("color pack");
			}
			userColorRepo.save(new UserColor(user, colorPack));
			user.setCoins(user.getCoins() - colorPack.getPrice());
			userRepo.save(user);
		}
	}

	/**
	 * saves the design base if no such base exists or fetches the existing base
	 * saves the design saves the design in the user list sets the design to be the
	 * user's chosen design
	 * 
	 * @param design
	 * @throws NoColorException - if the user does not own one or more of the
	 *                          colours for the design
	 */
	@Transactional(readOnly = false)
	public void addDesign(Design design) {
		if (design != null) {
			DesignBase base = design.getBase();
			String snakeCol = base.getSnakeColor();
			String borderCol = base.getBorderColor();
			String foodCol = base.getFoodColor();
			checkColorsInDesign(snakeCol, borderCol, foodCol);
			Optional<DesignBase> optionalBase = designBaseRepo.findBySnakeColorAndBorderColorAndFoodColor(snakeCol,
					borderCol, foodCol);
			base = optionalBase.isEmpty() ? designBaseRepo.save(base) : optionalBase.get();
			design.setBase(base);
			Design savedDesign = designRepo.save(design);
			userDesignRepo.save(new UserDesign(getUser().get(), savedDesign));
			setChosenDesign(savedDesign.getId());
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
	 * @throws NoDefaultDesignException - if no chosen design nor default design was found
	 */
	public Design getChosenDesign() {
		Optional<Design> optional = getDesign(getUser().get().getChosenDesign());
		if(optional.isPresent()) {
			return optional.get();
		}
		Optional<UserDesign> optionalDefault = userDesignRepo.findFirstByUserIdAndDesignName(detail.getId(), "default");
		if(optionalDefault.isEmpty()) {
			throw new NoDefaultDesignException();
		}
		return optionalDefault.get().getDesign();
	}
}
