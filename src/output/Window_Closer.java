package output;

//This plugin closes all the image windows without saving
import ij.*;
import ij.plugin.PlugIn;

public class Window_Closer implements PlugIn {
  public void run(String arg) {
      ImagePlus img;
      while (null != WindowManager.getCurrentImage()) {
          img = WindowManager.getCurrentImage();
          img.changes = false;
          img.close();
      }
  }
}
