package com.projects.snake.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projects.snake.exception.AlreadyPurchasedException;
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
		return Optional.of(responseMaker.make(user.getId(), user.getNickname(), user.getCoins(),
				getDesign(user.getChosenDesign())));
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

	private Design getDesign(Integer designId) {
		if (designId != null) {
			Optional<Design> optionalDesign = designRepo.findById(designId);
			if (optionalDesign.isPresent()) {
				return optionalDesign.get();
			}
		}
		// change to return the default design
		return null;
	}

	/**
	 * buys the colour pack
	 * 
	 * @param colorId
	 * @throws AlreadyPurchasedException - if the user already has this color pack
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
	
	public void addDesign(Design design) {
		if(design!=null) {
			
		}
	}
	
	private void checkColorsInDesign(Design design) {
		List<UserColor> userColors = userColorRepo.findByUserId(detail.getId());
		boolean snake = false;
		boolean border = false;
		boolean food = false;
		DesignBase base = design.getBase();
		for(int i=0; i<userColors.size() && (!snake || !border || !food); i++) {
			ColorPack colorPack = userColors.get(i).getColorPack();
			ColorType type = colorPack.getType();
			switch (type) {
			case SNAKE:
				snake = (snake || hasColor(snake, colorPack, base.getSnakeColor()));
				break;
			case BORDER:
				border = (border || hasColor(border, colorPack, base.getBorderColor()));
				break;

			default:
				food = (food || hasColor(food, colorPack, base.getFoodColor()));
				break;
			}
		}
		
	}
	
	private boolean hasColor(boolean found, ColorPack colorPack, String searchColor) {
		if(!found) {
			List<String> colors = colorPack.getColors();
			found = (found||colors.contains(searchColor));
		}
		return found;
	}
}
