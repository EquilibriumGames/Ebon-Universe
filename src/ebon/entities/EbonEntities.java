package ebon.entities;

import ebon.entities.loading.*;
import flounder.engine.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.models.*;
import flounder.physics.bounding.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.space.*;
import flounder.textures.*;

import java.io.*;
import java.lang.ref.*;
import java.util.*;

/**
 * A class that manages game entities.
 */
public class EbonEntities extends IModule {
	private static final EbonEntities instance = new EbonEntities();

	private StructureBasic<Entity> entityStructure;
	private Map<String, SoftReference<EntityTemplate>> loaded;

	/**
	 * Creates a new game manager for entities.
	 */
	public EbonEntities() {
		super(ModuleUpdate.AFTER_ENTRANCE, FlounderLogger.class, FlounderProfiler.class, FlounderModels.class, FlounderBounding.class, FlounderShaders.class, FlounderTextures.class);
	}

	@Override
	public void init() {
		this.entityStructure = new StructureBasic<>();
		this.loaded = new HashMap<>();
	}

	@Override
	public void run() {
		if (entityStructure != null) {
			entityStructure.getAll(new ArrayList<>()).forEach(Entity::update);
		}
	}

	@Override
	public void profile() {
		FlounderProfiler.add("Entities", "Count", entityStructure.getSize());
	}

	/**
	 * Clears the world of all entities.
	 */
	public static void clear() {
		instance.entityStructure.getAll(new ArrayList<>()).forEach(Entity::forceRemove);
	}

	/**
	 * Loads a entity name (folder) into a entity template.
	 *
	 * @param name The entity to load.
	 *
	 * @return The loaded entity template.
	 */
	public static EntityTemplate load(String name) {
		SoftReference<EntityTemplate> ref = instance.loaded.get(name);
		EntityTemplate data = ref == null ? null : ref.get();

		if (data == null) {
			FlounderLogger.log(name + " is being loaded into a entity template right now!");
			instance.loaded.remove(name);

			// Creates the file reader.
			MyFile saveFile = new MyFile(MyFile.RES_FOLDER, "entities", name, name + ".entity");

			try {
				BufferedReader fileReader = saveFile.getReader();

				if (fileReader == null) {
					FlounderLogger.error("Error creating reader the entity file: " + saveFile);
					return null;
				}

				// Loaded data.
				String entityName = "unnamed";
				Map<EntityIndividualData, List<EntitySectionData>> componentsData = new HashMap<>();

				// Current line.
				String line;

				// Each line read loop.
				while ((line = fileReader.readLine()) != null) {
					// Entity General Data.
					if (line.contains("EntityData")) {
						while (!(line = fileReader.readLine()).contains("};")) {
							if (line.contains("Name")) {
								entityName = line.replaceAll("\\s+", "").replaceAll(";", "").substring("Name:".length());
							}
						}
					}

					// Components.
					if (line.contains("Components")) {
						int fileNestation = 1;

						String componentClasspaths = null;
						String componentSubsection = null;

						List<Pair<String, String>> individualData = new ArrayList<>();
						List<String> sectionLines = new ArrayList<>();
						List<EntitySectionData> sections = new ArrayList<>();

						while (fileNestation > 0) {
							line = fileReader.readLine();

							if (line == null) {
								individualData.clear();
								sectionLines.clear();
								sections.clear();
								break;
							}

							if (line.contains("{")) {
								if (componentClasspaths == null) {
									componentClasspaths = line.replaceAll("\\s+", "");
									componentClasspaths = componentClasspaths.substring(0, componentClasspaths.length() - 1);
								} else {
									componentSubsection = line.replaceAll("\\s+", "");
									componentSubsection = componentSubsection.substring(0, componentSubsection.length() - 1);
								}

								fileNestation++;
							} else if (line.contains("};")) {
								fileNestation--;

								if (fileNestation == 1) {
									componentsData.put(new EntityIndividualData(componentClasspaths, new ArrayList<>(individualData)), sections);
									individualData.clear();
									componentClasspaths = null;
								} else if (componentSubsection != null) {
									sections.add(new EntitySectionData(componentSubsection, new ArrayList<>(sectionLines)));
									sectionLines.clear();
									componentSubsection = null;
								}
							} else if (!line.isEmpty()) {
								if (componentClasspaths != null && componentSubsection == null) {
									String[] lineKeys = line.replaceAll("\\s+", "").replace(";", "").trim().split(":");
									individualData.add(new Pair<>(lineKeys[0].trim(), lineKeys[1].trim()));
								} else if (componentSubsection != null) {
									sectionLines.addAll(Arrays.asList(line.replaceAll("\\s+", "").trim().split(",")));
								}
							}
						}
					}
				}

				data = new EntityTemplate(entityName, componentsData);
			} catch (IOException e) {
				FlounderLogger.error("File reader for entity " + saveFile.getPath() + " did not execute successfully!");
				FlounderLogger.exception(e);
				return null;
			}

			instance.loaded.put(name, new SoftReference<>(data));
		}

		return data;
	}

	/**
	 * Gets a list of entities.
	 *
	 * @return A list of entities.
	 */
	public static StructureBasic<Entity> getEntities() {
		return instance.entityStructure;
	}

	@Override
	public IModule getInstance() {
		return instance;
	}

	@Override
	public void dispose() {
		if (entityStructure != null) {
			entityStructure.clear();
			entityStructure = null;
		}
	}
}