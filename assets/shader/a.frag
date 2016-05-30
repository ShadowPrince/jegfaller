//define our sampler2D object, i.e. texture
uniform sampler2D tex0;

void main() {
	//get the unaltered color...
	vec4 c = texture2D(tex0, gl_TexCoord[0].st);
	//invert the color, leaving alpha intact
	float tresh = 0.1;
	float diff = 0.0;
	if (c.r > tresh && c.g > tresh && c.b > tresh) {
	    diff = +0.25;
	} else {
	    diff = -0.1;
	}

    gl_FragColor = vec4(c.rgb + diff, c.a);
} 