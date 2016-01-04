package q.baselibrary.image.image_picker;

import java.io.Serializable;

/**
 * 一个图片对象
 * 
 */
public class ImageItem implements Serializable {
	
	private static final long serialVersionUID = -3613750090880810573L;
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
	public boolean isSelected = false;
}
