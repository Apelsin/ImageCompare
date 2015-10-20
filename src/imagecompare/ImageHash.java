package imagecompare;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;

import java.io.PrintStream;
import java.util.Base64;

public class ImageHash implements Comparable<ImageHash>
{
    byte[] _ColorData;
    protected ImageHash()
    {
    }
    
    protected ImageHash(byte[] color_data)
    {
        this();
        _ColorData = color_data;
    }
    
    @Override
    public int compareTo(ImageHash o)
    {
        return Difference(o, false);
    }
    
    public int Difference(ImageHash o, boolean absolute)
    {
        int color_index = 0;
        int total_difference = 0;
        // i is resolution
        for(int i = 0;;i++)
        {
            // Two times i
            int ii = 2 * i;
            // How many pixels in this resolution
            int colors = 1 << ii;
            // For each pixel in the color data
            for(int j = 0; j < colors; j++)
            {
                int idx_byte = 3 * color_index;
                if(idx_byte < _ColorData.length && idx_byte < o._ColorData.length)
                {
                    int difference = 0;
                    int idx_b = idx_byte;
                    int idx_g = idx_byte + 1;
                    int idx_r = idx_byte + 2;
                    // BGR channel differences simply added together
                    if(absolute)
                    {
                        difference +=
                                Math.abs((int)o._ColorData[idx_b] -
                                ((int)_ColorData[idx_b])) + 
                                Math.abs((int)o._ColorData[idx_g] -
                                ((int)_ColorData[idx_g])) + 
                                Math.abs((int)o._ColorData[idx_r] -
                                ((int)_ColorData[idx_r]));
                    }
                    else
                    {
                        difference +=
                                ((int)o._ColorData[idx_b]) -
                                ((int)_ColorData[idx_b]) + 
                                ((int)o._ColorData[idx_g]) -
                                ((int)_ColorData[idx_g]) + 
                                ((int)o._ColorData[idx_r]) -
                                ((int)_ColorData[idx_r]);
                    }
                    // Be stupid and just add the color difference for each 
                    // resolution level together to the total difference 
                    // without weighing it at all.
                    // TODO: How to weigh each resolution??
                    total_difference += difference;
                    color_index++;
                }
                else
                    return total_difference;
            }
        }
    }
    
    public static ImageHash CreateFromImage(
            BufferedImage original_image, int resolution)
    {
        resolution = Math.max(1, resolution);
        // Given a resolution d, we will need an array of size
        // 3 * sum 2^(2k) for k 0..d
        int colors_count = 0;
        for(int i = resolution; i >= 0; i--)
            colors_count += (1 << (2 * i));
        byte[] color_data = new byte[3 * colors_count];
        // Start with the original
        BufferedImage resized = original_image;
        int color_index = colors_count;
        for(int i = resolution; i >= 0; i--)
        {
            int n = 1 << i;
            color_index -= 1 << (2 * i);
            resized = WriteResizedImageToColorArray(
                    resized,
                    BufferedImage.TYPE_3BYTE_BGR,
                    color_data,
                    n,
                    n,
                    3 * color_index);
        }
        return new ImageHash(color_data);
    }
    
    private static BufferedImage WriteResizedImageToColorArray(
            BufferedImage image,
            int color_format,
            byte[] array,
            int resize_width,
            int resize_height,
            int start_index)
    {
        Image scaled_instance = image.getScaledInstance(
                resize_width, resize_height, Image.SCALE_AREA_AVERAGING);
        BufferedImage image_resized  =
                new BufferedImage(resize_width, resize_height, color_format);
        Graphics2D g2d = (Graphics2D)image_resized.getGraphics();
        g2d.drawImage(scaled_instance, 0, 0, null);
        g2d.dispose();
        WriteDataToColorArray(
                image_resized,
                color_format,
                array,
                start_index);
        return image_resized;
    }
    
    private static void WriteDataToColorArray(
            BufferedImage image,
            int color_format,
            byte[] array,
            int start_index)
    {
        switch(color_format)
        {
            default:
            case BufferedImage.TYPE_3BYTE_BGR:
                DataBuffer buffer = image.getRaster().getDataBuffer();
                byte[] bgr_bytes = ((DataBufferByte)buffer).getData();
                System.arraycopy(
                        bgr_bytes, 0, array, start_index, bgr_bytes.length);
                break;
        }
       
    }
    
    public void Print(PrintStream stream)
    {
        Base64.Encoder encoder = Base64.getEncoder();
        stream.print(encoder.encodeToString(_ColorData));
    }
}
