import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

import imagecompare.ImageHash;

import net.sourceforge.argparse4j.inf.Namespace;

public class Commands
{
    public static void Compare(Namespace arg_ns) throws IOException
    {
        String reference_hash = arg_ns.getString("-R");
        if(reference_hash != null)
        {
            // Compare hash with images
        }
        else
        {
            // Compare reference image with images
            String path_image_reference = arg_ns.getString("reference");
            List<String> paths_images = arg_ns.getList("images");
            
            int degree = Integer.parseInt(arg_ns.getString("degree"));
            
            File file_image_reference = new File(path_image_reference);
            BufferedImage image_reference = ImageIO.read(file_image_reference);
            ImageHash hash_reference = ImageHash.CreateFromImage(image_reference, degree);
            
            int zero_count = (int)Math.ceil(Math.log10(paths_images.size()));
            
            int width = 10;
            
            String format_image_number;
            if(zero_count > 0)
                format_image_number = "%0" + zero_count + "d";
            else
                format_image_number = "%d";
            String format_line = "%" + width + "s: %s";
            
            System.out.println(String.format(
                    format_line,
                    "Reference",
                    hash_reference.GetBase64String()));
            
            for(int i = 0; i < paths_images.size(); i++)
            {
                String path_image = paths_images.get(i);
                File file_image = new File(path_image);
                BufferedImage image = ImageIO.read(file_image);
                ImageHash hash = ImageHash.CreateFromImage(image, degree);
                
                String image_number = String.format(format_image_number, i);
                System.out.println(String.format(
                        format_line,
                        "Image " + image_number,
                        hash.GetBase64String()));
                
                float difference = hash_reference.DifferenceMultiResolution(
                        hash, ImageHash.DifferenceMode.Absolute);
                float similarity = 1f - difference;
                String percentage = String.format("%.2f", 100f * similarity);
                percentage = percentage.replaceAll("\\.0+$", "");
                
                System.out.println(String.format(
                        format_line,
                        "Similarity",
                        percentage));
            }
        }
    }
    public static void Hash(Namespace arg_ns) throws IOException
    {
        String image_file_path = arg_ns.getString("image");
        int degree;
        try
        {
            degree = Integer.parseInt(arg_ns.getString("degree"));
        }
        catch(NumberFormatException ex)
        {
            degree = 2;
        }
        
        File image_file = new File(image_file_path);
        BufferedImage image = ImageIO.read(image_file);
        
        ImageHash hash = ImageHash.CreateFromImage(image, degree);
        System.out.println(hash.GetBase64String());
    }
    public static void Test(Namespace arg_ns)
    {
    }
}
