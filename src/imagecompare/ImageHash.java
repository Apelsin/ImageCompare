package imagecompare;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;

import java.util.Base64;

public class ImageHash implements Comparable<ImageHash>
{
    private byte[] _ColorData;
    private int _Degree;
    protected ImageHash()
    {
    }
    
    @SuppressWarnings("empty-statement")
    protected ImageHash(byte[] color_data)
    {
        this();
        _ColorData = color_data;
        if(_ColorData != null)
        {
            // Calculate the degree of _ColorData
            int colors_count = _ColorData.length / 3;
            _Degree = 0;
            while(1 << (2 *_Degree + 1) <= colors_count)
                _Degree++;
        }
    }
    
    protected ImageHash(byte[] color_data, int degree)
    {
        this();
        _ColorData = color_data;
        _Degree = degree;
    }
    
    public int GetDegree()
    {
        return _Degree;
    }
    
    @Override
    public int compareTo(ImageHash o)
    {
        Float difference = Difference(o, DifferenceMode.Subtract);
        if(difference > 0)
            return 1;
        else if(difference < 0)
            return -1;
        return 0;
    }
    
    public enum DifferenceMode
    {
        Subtract,
        Absolute,
        RootMeanSquare,
    }
    
    public float DifferenceMultiResolution(ImageHash o, DifferenceMode mode)
    {
        float difference = Difference(o, mode);
        BufferedImage this_image = GetImage();
        BufferedImage o_image = o.GetImage();
        for(int i = _Degree - 1; i >= 0; i--)
        {
            int colors_count = 1 << (2 * i);
            ImageHash this_reduced = ImageHash.CreateFromImage(this_image, i);
            ImageHash o_reduced = ImageHash.CreateFromImage(o_image, i);
            difference += this_reduced.Difference(o_reduced, mode);
        }
        return difference / _Degree;
    }
    
    public BufferedImage GetImage()
    {
        int n = 1 << _Degree;
        BufferedImage image = new BufferedImage(n, n, BufferedImage.TYPE_3BYTE_BGR);
        byte[] buffer = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
        System.arraycopy(_ColorData, 0, buffer, 0, _ColorData.length);
        return image;
    }
    
    public float Difference(ImageHash o, DifferenceMode mode)
    {
        // Counting differences
        float difference = 0;
        
        // TODO: if hashes are of different degrees (length), reduce the hash 
        // of higher degree to the same degree of the other
        
        // System.out.println("this._Degree = " + this._Degree);
        
        int min_degree = Math.min(this._Degree,  o._Degree);
        int colors_count = 1 << (2 * min_degree);
        int difference_divisor = 0x300 * colors_count;
        
        //System.out.println("colors_count = " + colors_count);
        
        for(int i = 0; i < colors_count; i++)
        {
            int idx_b = 3 * i;
            int idx_g = idx_b + 1;
            int idx_r = idx_b + 2;
            
            int diff_b = ((int)o._ColorData[idx_b]) - ((int)_ColorData[idx_b]);
            int diff_g = ((int)o._ColorData[idx_g]) - ((int)_ColorData[idx_g]);
            int diff_r = ((int)o._ColorData[idx_r]) - ((int)_ColorData[idx_r]);
            
            switch(mode)
            {
                default:
                case Subtract:
                    difference +=
                            diff_b +
                            diff_g +
                            diff_r;
                break;
                case Absolute:
                    difference +=
                            Math.abs(diff_b) +
                            Math.abs(diff_g) +
                            Math.abs(diff_r);
                    
                break;
                case RootMeanSquare:
                    difference +=
                            diff_b * diff_b +
                            diff_g * diff_g +
                            diff_r * diff_r;
                break;
            }
        }
        float mean = (float)difference / (float)difference_divisor;
        switch(mode)
        {
            default:
            case Subtract:
            case Absolute:
                return mean;
            case RootMeanSquare:
                return (float)Math.sqrt(mean / (float)difference_divisor);
        }
    }
    
    public static ImageHash CreateFromImage(
            BufferedImage original_image, int degree)
    {
        degree = Math.max(0, degree);
        // Given a resolution d, we will need an array of size 2^(2d)
        int n = 1 << degree;
        int colors_count = 1 << (2 * degree);
        byte[] color_data = new byte[3 * colors_count];
        // Take the original image and resize it to n*n
        WriteResizedImageToColorArray(
                original_image,
                BufferedImage.TYPE_3BYTE_BGR,
                color_data,
                n,
                n,
                0);
        return new ImageHash(color_data, degree);
    }
    
    public static ImageHash CreateFromBase64String(String b64)
    {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] color_data = decoder.decode(b64);
        return new ImageHash(color_data);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Array Writing">
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
    // </editor-fold>
    
    public String GetBase64String()
    {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(_ColorData);
    }
}
