#ifdef GL_ES

#precision mediump float

#endif
#define PROCESSING_COLOR_SHADER

uniform float iGlobalTime; 
uniform vec2 iResolution;

void main(void)
{
	float x = gl_FragCoord.x / iResolution.x;
    float y = gl_FragCoord.y / iResolution.y;
    
	gl_FragColor = vec4(abs(sin(10.0*x+iGlobalTime*2.0)), abs(sin(12.0*y*(1.2+sin(iGlobalTime*2.0)))), abs(sin(iGlobalTime*4.0)), 1.0);
}
