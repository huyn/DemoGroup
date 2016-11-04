package com.huyn.demogroup.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.database.Cursor;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;

public class OpenGLRenderer implements Renderer {
	
	// Initialize our square.
	Square square = new SmoothColorSquare();
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.
         * microedition.khronos.opengles.GL10, javax.microedition.khronos.
         * egl.EGLConfig)
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set the background color to black ( rgba ).
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);  // OpenGL docs.
		// Enable Smooth Shading, default not really needed.
		gl.glShadeModel(GL10.GL_SMOOTH);// OpenGL docs.
		// Depth buffer setup.
		gl.glClearDepthf(1.0f);// OpenGL docs.
		// Enables depth testing.
		gl.glEnable(GL10.GL_DEPTH_TEST);// OpenGL docs.
		// The type of depth testing to do.
		gl.glDepthFunc(GL10.GL_LEQUAL);// OpenGL docs.
		// Really nice perspective calculations.
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, // OpenGL docs.
                          GL10.GL_NICEST);
		
		

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.
         * microedition.khronos.opengles.GL10)
	 */
	public void onDrawFrame(GL10 gl) {
//		drawSolarSystem(gl);
		drawSquare(gl);
	}

	private void drawSquare(GL10 gl) {
		// Clears the screen and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | // OpenGL docs.
				GL10.GL_DEPTH_BUFFER_BIT);
		//important!!! to reset matrix
		gl.glLoadIdentity();
		//Translates 14 units into the screen.
		gl.glTranslatef(0,0, -14);
		// Draw our square.
		square.draw(gl); // ( NEW )
	}

	int angle = 0;
	public void drawSolarSystem(GL10 gl) {
		// Clears the screen and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT
				| GL10.GL_DEPTH_BUFFER_BIT);
		// Replace the current matrix with the identity matrix
		gl.glLoadIdentity();
		// Translates 10 units into the screen.
		gl.glTranslatef(0, 0, -10);

		// SQUARE A
		// Save the current matrix.
		gl.glPushMatrix();
		// Rotate square A counter-clockwise.
		gl.glRotatef(angle, 0, 0, 1);
		// Draw square A.
		square.draw(gl);
		// Restore the last matrix.
		gl.glPopMatrix();

		// SQUARE B
		// Save the current matrix
		gl.glPushMatrix();
		// Rotate square B before moving it,
		//making it rotate around A.
		gl.glRotatef(-angle, 0, 0, 1);
		// Move square B.
		gl.glTranslatef(2, 0, 0);
		// Scale it to 50% of square A
		gl.glScalef(.5f, .5f, .5f);
		// Draw square B.
		square.draw(gl);

		// SQUARE C
		// Save the current matrix
		gl.glPushMatrix();
		// Make the rotation around B
		gl.glRotatef(-angle, 0, 0, 1);
		gl.glTranslatef(2, 0, 0);
		// Scale it to 50% of square B
		gl.glScalef(.5f, .5f, .5f);
		// Rotate around it's own center.
		gl.glRotatef(angle*10, 0, 0, 1);
		// Draw square C.
		square.draw(gl);

		// Restore to the matrix as it was before C.
		gl.glPopMatrix();
		// Restore to the matrix as it was before B.
		gl.glPopMatrix();

		// Increse the angle.
		angle++;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.
         * microedition.khronos.opengles.GL10, int, int)
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// Sets the current view port to the new size.
		gl.glViewport(0, 0, width, height);// OpenGL docs.
		// Select the projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);// OpenGL docs.
		// Reset the projection matrix
		gl.glLoadIdentity();// OpenGL docs.
		// Calculate the aspect ratio of the window
		GLU.gluPerspective(gl, 45.0f,
                                   (float) width / (float) height,
                                   0.1f, 100.0f);
		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);// OpenGL docs.
		// Reset the modelview matrix
		gl.glLoadIdentity();// OpenGL docs.
	}
}
