#ifdef GL_ES
#precision mediump float
#precision mediump int
#endif

uniform float iGlobalTime; 
uniform vec2 iResolution;

void main(void)
{
	float x = gl_FragCoord.x / iResolution.x;
    float y = gl_FragCoord.y / iResolution.y;
    
    float target_x = 0.5 + 0.5 * sin(iGlobalTime*2.0);
    float target_y = 0.5 + 0.5 * cos(iGlobalTime*2.0);
    float target_z = 0.5 - 0.5 * cos(iGlobalTime*2.0);
	
    float red = abs(x-target_x) + abs(y-target_y);
    float green = abs(x-target_x) + abs(y-target_z);
    float blue = abs(x-target_z) + abs(y-target_z);
    gl_FragColor = vec4(red, green, blue, 1.0);
}
