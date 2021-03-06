package ebon.entities.components;

import flounder.entities.*;
import flounder.entities.components.*;
import flounder.entities.template.*;
import flounder.materials.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.*;
import flounder.resources.*;
import flounder.textures.*;

/**
 * Creates a model with a texture that can be rendered into the world.
 */
public class ComponentModel extends IComponentEntity {
	public static final int ID = EntityIDAssigner.getId();

	private Model model;
	private float scale;
	private Matrix4f modelMatrix;

	private Texture texture;
	private Texture normalMap;
	private float transparency;
	private int textureIndex;

	/**
	 * Creates a new ComponentModel.
	 *
	 * @param entity The entity this component is attached to.
	 * @param model The model that will be attached to this entity.
	 * @param scale The scale of the entity.
	 * @param texture The diffuse texture for the entity.
	 * @param normalMap The normalmap texture for the entity.
	 * @param textureIndex What texture index this entity should renderObjects from (0 default).
	 */
	public ComponentModel(Entity entity, Model model, float scale, Texture texture, Texture normalMap, int textureIndex) {
		super(entity, ID);
		this.model = model;
		this.scale = scale;
		this.modelMatrix = new Matrix4f();

		this.texture = texture;
		this.normalMap = normalMap;
		this.transparency = 1.0f;
		this.textureIndex = textureIndex;
	}

	/**
	 * Creates a new ComponentModel. From strings loaded from entity files.
	 *
	 * @param entity The entity this component is attached to.
	 * @param template The entity template to load data from.
	 */
	public ComponentModel(Entity entity, EntityTemplate template) {
		super(entity, ID);

		this.model = Model.newModel(new ModelBuilder.LoadManual() {
			@Override
			public String getModelName() {
				return template.getEntityName();
			}

			@Override
			public float[] getVertices() {
				return EntityTemplate.toFloatArray(template.getSectionData(ComponentModel.this, "Vertices"));
			}

			@Override
			public float[] getTextureCoords() {
				return EntityTemplate.toFloatArray(template.getSectionData(ComponentModel.this, "TextureCoords"));
			}

			@Override
			public float[] getNormals() {
				return EntityTemplate.toFloatArray(template.getSectionData(ComponentModel.this, "Normals"));
			}

			@Override
			public float[] getTangents() {
				return EntityTemplate.toFloatArray(template.getSectionData(ComponentModel.this, "Tangents"));
			}

			@Override
			public int[] getIndices() {
				return EntityTemplate.toIntArray(template.getSectionData(ComponentModel.this, "Indices"));
			}

			@Override
			public Material[] getMaterials() {
				return new Material[]{}; // TODO: Save and load materials!
			}

			@Override
			public AABB getAABB() {
				return null; // TODO: Load AABB.
			}

			@Override
			public QuickHull getHull() {
				return null; // TODO: Load hull.
			}
		}).create();

		this.scale = Float.parseFloat(template.getValue(this, "Scale"));
		this.modelMatrix = new Matrix4f();

		if (!template.getValue(this, "Texture").equals("null")) {
			this.texture = Texture.newTexture(new MyFile(template.getValue(this, "Texture"))).create();
			this.texture.setNumberOfRows(Integer.parseInt(template.getValue(this, "TextureNumRows")));
		}

		if (!template.getValue(this, "NormalMap").equals("null")) {
			this.normalMap = Texture.newTexture(new MyFile(template.getValue(this, "NormalMap"))).create();
			this.normalMap.setNumberOfRows(Integer.parseInt(template.getValue(this, "NormalMapNumRows")));
		}

		this.transparency = 1.0f;
	}

	@Override
	public void update() {
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	/**
	 * Gets the entitys model matrix.
	 *
	 * @return The entitys model matrix.
	 */
	public Matrix4f getModelMatrix() {
		modelMatrix.setIdentity();
		float scale = 1.0f;
		scale = (super.getEntity().getComponent(ComponentModel.ID) != null) ? ((ComponentModel) super.getEntity().getComponent(ComponentModel.ID)).getScale() : scale;
		Matrix4f.transformationMatrix(super.getEntity().getPosition(), super.getEntity().getRotation(), scale, modelMatrix);
		return modelMatrix;
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	/**
	 * Gets the textures coordinate offset that is used in rendering the model.
	 *
	 * @return The coordinate offset used in rendering.
	 */
	public Vector2f getTextureOffset() {
		int column = textureIndex % texture.getNumberOfRows();
		int row = textureIndex / texture.getNumberOfRows();
		return new Vector2f((float) row / (float) texture.getNumberOfRows(), (float) column / (float) texture.getNumberOfRows());
	}

	public Texture getNormalMap() {
		return normalMap;
	}

	public void setNormalMap(Texture normalMap) {
		this.normalMap = normalMap;
	}

	public float getTransparency() {
		return transparency;
	}

	public void setTransparency(float transparency) {
		this.transparency = transparency;
	}

	public void setTextureIndex(int index) {
		this.textureIndex = index;
	}

	@Override
	public IBounding getBounding() {
		return null;
	}

	@Override
	public void dispose() {
	}
}
