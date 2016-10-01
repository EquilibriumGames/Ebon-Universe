package editor.entities.components;

import ebon.entities.*;
import ebon.entities.components.*;
import ebon.entities.loading.*;
import flounder.helpers.*;

import javax.swing.*;

public class EditorCollision extends IEditorComponent {
	public ComponentCollision component;

	public EditorCollision(Entity entity) {
		this.component = new ComponentCollision(entity);
	}

	public EditorCollision(IEntityComponent component) {
		this.component = (ComponentCollision) component;
	}

	@Override
	public String getTabName() {
		return "Collision";
	}

	@Override
	public ComponentCollision getComponent() {
		return component;
	}

	@Override
	public void addToPanel(JPanel panel) {
		// Nothing to add.
	}

	@Override
	public Pair<String[], EntitySaverFunction[]> getSavableValues() {
		return new Pair<>(new String[]{}, new EntitySaverFunction[]{});
	}
}
