package me.ghui.v2er.util;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

import me.ghui.v2er.general.Pref;

/**
 * Created by ghui on 24/10/2017.
 */

public class TexureUtil {
    private static int maxSize = -1;
    private static String KEY = "max_texture_size";


    public static int fitMaxWidth() {
        return (int) (1.5f * ScaleUtils.getScreenW());
    }

    public static int fitMaxHeight() {
        if (maxSize > 0) return maxSize;
        maxSize = Pref.readInt(KEY);
        if (maxSize > 0) {
            return maxSize;
        }
        // Safe minimum default size
        final int IMAGE_MAX_BITMAP_DIMENSION = 2048;
        // Get EGL Display
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        // Initialise
        int[] version = new int[2];
        egl.eglInitialize(display, version);

        // Query total number of configurations
        int[] totalConfigurations = new int[1];
        egl.eglGetConfigs(display, null, 0, totalConfigurations);

        // Query actual list configurations
        EGLConfig[] configurationsList = new EGLConfig[totalConfigurations[0]];
        egl.eglGetConfigs(display, configurationsList, totalConfigurations[0], totalConfigurations);

        int[] textureSize = new int[1];
        int maximumTextureSize = 0;

        // Iterate through all the configurations to located the maximum texture size
        for (int i = 0; i < totalConfigurations[0]; i++) {
            // Only need to check for width since opengl textures are always squared
            egl.eglGetConfigAttrib(display, configurationsList[i], EGL10.EGL_MAX_PBUFFER_WIDTH, textureSize);

            // Keep track of the maximum texture size
            if (maximumTextureSize < textureSize[0])
                maximumTextureSize = textureSize[0];
        }
        // Release
        egl.eglTerminate(display);
        // Return largest texture size found, or default
        maxSize = (int) (Math.max(maximumTextureSize * 0.6, IMAGE_MAX_BITMAP_DIMENSION));
        Pref.save(KEY, maxSize);
        return maxSize;
    }

}
