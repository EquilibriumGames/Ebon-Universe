package game.players;

import flounder.engine.*;
import flounder.inputs.*;
import flounder.maths.*;
import flounder.maths.rays.*;
import flounder.maths.vectors.*;
import flounder.physics.*;
import game.*;
import game.celestial.*;
import game.options.*;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerFPS implements IPlayer {
	private static final AABB PLAYER_AABB = new AABB(new Vector3f(-1, -1, -1), new Vector3f(1, 1, 1));
	private static final float SPEED_BOOST_SCALE = 2.75f;
	private static final float FRONT_SPEED = 60;
	private static final float UP_SPEED = 40;
	private static final float SIDE_SPEED = 60;

	private IAxis inputForward;
	private IAxis inputUp;
	private IAxis inputRoll;
	private IAxis inputSide;
	private CompoundButton inputSpeedBoost;

	private Vector3f velocity;

	private Vector3f position;
	private Vector3f rotation;

	private AABB starViewAABB;
	private Ray starViewRay;
	private Star currentStar;

	@Override
	public void init() {
		IButton leftKeyButtons = new KeyButton(GLFW_KEY_A, GLFW_KEY_LEFT);
		IButton rightKeyButtons = new KeyButton(GLFW_KEY_D, GLFW_KEY_RIGHT);
		IButton forwardsKeyButtons = new KeyButton(GLFW_KEY_W, GLFW_KEY_UP);
		IButton backwardsKeyButtons = new KeyButton(GLFW_KEY_S, GLFW_KEY_DOWN);
		IButton downKeyButtons = new KeyButton(GLFW_KEY_LEFT_CONTROL);
		IButton upKeyButtons = new KeyButton(GLFW_KEY_SPACE);
		IButton rollLeftButtons = new KeyButton(GLFW_KEY_Q);
		IButton rollRightButtons = new KeyButton(GLFW_KEY_A);

		this.inputForward = new CompoundAxis(new ButtonAxis(backwardsKeyButtons, forwardsKeyButtons), new JoystickAxis(OptionsControls.JOYSTICK_PORT, OptionsControls.JOYSTICK_AXIS_Y));
		this.inputUp = new CompoundAxis(new ButtonAxis(downKeyButtons, upKeyButtons), new ButtonAxis(new JoystickButton(OptionsControls.JOYSTICK_PORT, OptionsControls.JOYSTICK_CAMERA_DOWN), new JoystickButton(OptionsControls.JOYSTICK_PORT, OptionsControls.JOYSTICK_CAMERA_UP)));
		this.inputRoll = new CompoundAxis(new ButtonAxis(rollLeftButtons, rollRightButtons));
		this.inputSide = new CompoundAxis(new ButtonAxis(leftKeyButtons, rightKeyButtons), new JoystickAxis(OptionsControls.JOYSTICK_PORT, OptionsControls.JOYSTICK_AXIS_X));
		this.inputSpeedBoost = new CompoundButton(new KeyButton(GLFW_KEY_LEFT_SHIFT), new JoystickButton(OptionsControls.JOYSTICK_PORT, OptionsControls.JOYSTICK_GUI_LEFT));

		this.velocity = new Vector3f(0, 0, 0);

		this.position = new Vector3f(0.0f, 0.0f, 0.0f);
		this.rotation = new Vector3f(0, 0, 0);

		this.starViewAABB = new AABB(new Vector3f(-50.0f, -50.0f, -50.0f), new Vector3f(50.0f, 50.0f, 50.0f));
		this.starViewRay = new Ray(false, new Vector2f(0.5f, 0.5f));
	}

	@Override
	public void update(boolean paused) {
		if (!paused) {
			float speedBoost = inputSpeedBoost.isDown() ? SPEED_BOOST_SCALE : 1.0f;
			rotation.set(0.0f, FlounderEngine.getCamera().getYaw(), 0.0f);
			velocity.x = speedBoost * SIDE_SPEED * FlounderEngine.getDelta() * Maths.deadband(0.05f, inputSide.getAmount());
			velocity.y = speedBoost * UP_SPEED * FlounderEngine.getDelta() * Maths.deadband(0.05f, inputUp.getAmount());
			velocity.z = speedBoost * FRONT_SPEED * FlounderEngine.getDelta() * -Maths.deadband(0.05f, inputForward.getAmount());
			Vector3f.rotate(velocity, rotation, velocity);
			Vector3f.add(position, velocity, position);

			starViewRay.update(FlounderEngine.getCamera().getPosition());

		//	for (int i = 0; i < 6; i++) {
		//		FlounderEngine.getLogger().error(starViewRay.getPointAtDistance(i * 70));
		//	}

			for (Star star : Environment.getStars().queryInAABB(new ArrayList<>(), starViewAABB)) {
				FlounderEngine.getAABBs().addAABBRender(star.getAABB());

				if (star.getAABB().intersectsRay(starViewRay, 0.0f, 100.0f) != null) {
					if (!star.equals(currentStar)) {
						FlounderEngine.getLogger().log("Camera ray hit star: " + star);
						currentStar = star;
					}

					if (!star.isChildrenLoaded()) {
						star.loadChildren();
					//	Star.printSystem(star);
					}
				}
			}

			FlounderEngine.getAABBs().addAABBRender(starViewAABB);

			/*AABB.recalculate(PLAYER_AABB, starViewAABB, position, rotation, 1.0f);
			List<Star> touchingStars = Environment.getStars().queryInAABB(new ArrayList<>(), starViewAABB);

			if (!touchingStars.isEmpty()) {
				if (touchingStars.size() > 0) {
					touchingStars = ArraySorting.quickSort(touchingStars);
					Collections.reverse(touchingStars);
				}

				Star star = touchingStars.get(0);

				if (!star.isChildrenLoaded()) {
					star.loadChildren();
					Star.printSystem(star);
				//	((MainGame) FlounderEngine.getGame()).switchCamera();
				}// else {
				//	System.out.println(star);
				//}
			}*/
		}
	}

	@Override
	public Vector3f getPosition() {
		return position;
	}

	@Override
	public Vector3f getRotation() {
		return rotation;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	@Override
	public void dispose() {
	}
}
