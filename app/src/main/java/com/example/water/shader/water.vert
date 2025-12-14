attribute float x;
uniform float surface[200];

void main() {
    float y = surface[int(x)];
    gl_Position = vec4(
        (x / 100.0) - 1.0,
        y,
        0.0,
        1.0
    );
}
