#version 130

layout(location = 0) out vec4 out_colour;

varying vec2 pass_textureCoords;

layout(binding = 0) uniform sampler2D colourTexture;
layout(binding = 1) uniform sampler2D positionTexture;
layout(binding = 2) uniform sampler2D normalsTexture;
layout(binding = 3) uniform sampler2D specularTexture;

const int NUMBER_LIGHTS = 32;

uniform vec3 cameraPosition;
uniform vec3 lightsColour[NUMBER_LIGHTS];
uniform vec3 lightsPosition[NUMBER_LIGHTS];
uniform vec3 lightsAttenuation[NUMBER_LIGHTS];

void main(void) {
    vec4 colour = texture2D(colourTexture, pass_textureCoords);
    vec4 position = texture2D(positionTexture, pass_textureCoords);
    vec4 normal = texture2D(normalsTexture, pass_textureCoords);
    vec4 specular = texture2D(specularTexture, pass_textureCoords);

//    for (int i = 0; i < NUMBER_LIGHTS; i++) {
//    }
    out_colour = normal;

/*    if (pass_textureCoords.x < 0.499 && pass_textureCoords.y < 0.499) {
        // Quad 3
	    out_colour = colour;
    } else if (pass_textureCoords.x > 0.50 && pass_textureCoords.y < 0.499) {
        // Quad 4
	    out_colour = position;
    } else if (pass_textureCoords.x < 0.499 && pass_textureCoords.y > 0.50) {
        // Quad 1
	    out_colour = normal;
    } else if (pass_textureCoords.x > 0.50 && pass_textureCoords.y > 0.50) {
        // Quad 2
	    out_colour = specular;
    } else {
        // Border Area.
        out_colour = vec4(0.0, 0.5, 1.0, 1.0);
    }*/
}
