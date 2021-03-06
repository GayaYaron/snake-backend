package com.projects.snake.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projects.snake.exception.AlreadyPurchasedException;
import com.projects.snake.exception.LoginFailedException;
import com.projects.snake.exception.NoColorException;
import com.projects.snake.exception.NoDefaultDesignException;
import com.projects.snake.exception.NotEnoughCoinsException;
import com.projects.snake.exception.NotFoundException;
import com.projects.snake.exception.util.NullUtil;
import com.projects.snake.model.ColorPack;
import com.projects.snake.model.ColorToPack;
import com.projects.snake.model.ColorType;
import com.projects.snake.model.Design;
import com.projects.snake.model.StringEnt;
import com.projects.snake.model.User;
import com.projects.snake.model.UserColor;
import com.projects.snake.repository.ColorPackRepo;
import com.projects.snake.repository.ColorToPackRepo;
import com.projects.snake.repository.DesignRepo;
import com.projects.snake.repository.StringEntRepo;
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
	private ColorPackRepo colorPackRepo;
	@Autowired
	private UserColorRepo userColorRepo;
	@Autowired
	private StringEntRepo stringEntRepo;
	@Autowired
	private ColorToPackRepo colorToPackRepo;
	@Autowired
	private LoginResponseMaker responseMaker;
	@Autowired
	private NullUtil nullUtil;
	@Autowired
	private UserDetail detail;

	/**
	 * registers the new user with 50 coins and a default chosen design
	 * 
	 * @param nickname
	 * @param password
	 * @return login response of the new user
	 */
	public LoginResponse register(String nickname, String password) {
		User user = userRepo.save(new User(nickname, password, 50));
		Design design = designRepo.save(new Design("default", "backGrey", "baclBlack", "backRed", user));
		user.setChosenDesign(design.getId());
		userRepo.save(user);
		return login(nickname, password);
	}

	/**
	 * 
	 * @param nickname
	 * @param password
	 * @return login response
	 * @throws LoginFailedException - if no such user was found
	 * @throws NullException        - if nickname and/or password is null
	 */
	public LoginResponse login(String nickname, String password) {
		nullUtil.check(nickname, "nickname");
		nullUtil.check(password, "password");
		Optional<User> optionalUser = userRepo.findByNicknameAndPassword(nickname, password);
		if (optionalUser.isEmpty()) {
			throw new LoginFailedException();
		}
		User user = optionalUser.get();
		return responseMaker.make(user.getId(), user.getChosenDesign(), user.getNickname(), user.getCoins());
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
			if (!(designRepo.existsByIdAndUserId(designId, detail.getId()))) {
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
	 * buys the colour pack (adds the purchase and reduces the user's coins)
	 * 
	 * @param colorId
	 * @throws AlreadyPurchasedException - if the user already has this colour pack
	 * @throws NotFoundException         - if such colour pack was not found
	 * @throws NotEnoughCoinsException   - if the user does not have enough coins to
	 *                                   buy the colour pack
	 */
	@Transactional(readOnly = false)
	public void buyColorPack(Integer colorId) {
		if (colorId != null) {
			if (userColorRepo.existsByUserIdAndColorPackId(detail.getId(), colorId)) {
				throw new AlreadyPurchasedException("color pack");
			}
			Optional<ColorPack> optional = colorPackRepo.findById(colorId);
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
			List<ColorToPack> colors = colorPack.getColors();
			for (int i = 0; i < colors.size() && !found; i++) {
				found = colors.get(i).getColor().getValue() == searchColor;
			}
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
		Integer chosenDesign = detail.getChosenDesignId();
		if (chosenDesign != null) {
			Optional<Design> optionalChosen = designRepo.findById(chosenDesign);
			if (optionalChosen.isPresent()) {
				return optionalChosen.get();
			}
		}
		Optional<Design> optionalDefault = designRepo.findFirstByNameAndUserId("default", detail.getId());
		if (optionalDefault.isEmpty()) {
			throw new NoDefaultDesignException();
		}
		return optionalDefault.get();
	}

	/**
	 * deletes the design as long as it is not called default or that the user has
	 * another default design
	 * 
	 * @param designId
	 * @throws NoDefaultDesignException - if it is the only default design the user
	 *                                  has
	 */
	@Transactional(readOnly = false)
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

	/**
	 * 
	 * @return a list of all the user's designs
	 */
	public List<Design> getUserDesigns() {
		return designRepo.findByUserId(detail.getId());
	}

	/**
	 * adds the amount to the user's coins
	 * 
	 * @param amount
	 */
	@Transactional(readOnly = false)
	public int addCoins(int amount) {
		User user = getUser().get();
		user.setCoins(user.getCoins() + amount);
		User savedUser = userRepo.save(user);
		return savedUser.getCoins();
	}

	/**
	 * saves the colour pack and saves all the colours and their relation to the
	 * pack
	 * 
	 * @param pack
	 * @param colors
	 * @return the saved colour pack with id (without colours)
	 */
	public ColorPack addColorPack(ColorPack pack, List<String> colors) {
		ColorPack colorPack = colorPackRepo.save(pack);
		if (colors != null) {
			for (String color : colors) {
				Optional<StringEnt> optionalColor = stringEntRepo.findById(color);
				StringEnt colorEnt = (optionalColor.isPresent()) ? optionalColor.get()
						: stringEntRepo.save(new StringEnt(color));
				colorToPackRepo.save(new ColorToPack(colorEnt, colorPack));
			}
		}
		return colorPack;
	}

	/**
	 * 
	 * @return a list of all the user's colour packs
	 */
	public List<ColorPack> getUserColorPacks() {
		return userColorRepo.findByUserId(detail.getId()).stream().map(userColor -> userColor.getColorPack())
				.collect(Collectors.toList());
	}
	
	/**
	 * 
	 * @return a list of all the colour packs
	 */
	public List<ColorPack> getColorPacks() {
		return colorPackRepo.findAll();
	}
}
