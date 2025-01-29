package utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

public class CloudinaryImageUpload {
    public static String uploadImage(String imagePath) throws Exception {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "cloudinart_name",
                "api_key", "cloudinary_key",
                "api_secret", "cloudinary_secret"));

        File imageFile = new File(imagePath);
        Map uploadResult = cloudinary.uploader().upload(imageFile, ObjectUtils.emptyMap());
        return (String) uploadResult.get("url");
    }

    public static File convertToPng(String imagePath) throws Exception {
        BufferedImage image = ImageIO.read(new File(imagePath));
        File pngFile = new File("C:/Users/Admin/Pictures/Screenshots/a1qa.png");
        ImageIO.write(image, "png", pngFile);
        return pngFile;
    }
}
