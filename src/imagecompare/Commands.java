package imagecompare;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

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
            String reference_image_path = arg_ns.getString("reference");
            List<String> image_paths = arg_ns.getList("images");
            int degree;
            try
            {
                degree = Integer.parseInt(arg_ns.getString("degree"));
            }
            catch(NumberFormatException ex)
            {
                degree = 2;
            }

            File reference_image_file = new File(reference_image_path);
            BufferedImage image = ImageIO.read(reference_image_file);
            
            File image_file_0 = new File(image_paths.get(0));
            BufferedImage image_0 = ImageIO.read(image_file_0);

            ImageHash hash_reference = ImageHash.CreateFromImage(image, degree);
            ImageHash hash_image_0 = ImageHash.CreateFromImage(image_0, degree);
            
            System.out.print(" Reference: ");
            System.out.println(hash_reference.GetBase64String());
            System.out.print("   Image 0: ");
            System.out.println(hash_image_0.GetBase64String());
            System.out.print("Similarity: ");
            float difference = hash_reference.DifferenceMultiResolution(
                    hash_image_0, ImageHash.DifferenceMode.Absolute);
            float similarity = 1f - difference;
            String percentage = String.format("%.2f", 100f * similarity);
            percentage = percentage.replaceAll("\\.0+$", "");
            System.out.println(percentage + "%");
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
