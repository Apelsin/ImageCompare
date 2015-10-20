package imagecompare;

import java.util.Base64;

public class ColorRGB8
{
    // Using shorts (16 bits) in order to account for negative color difference
    // TODO: use RGB+S bytes (sign bits in separate byte)
    public short Red;
    public short Green;
    public short Blue;
    
    String _Encoded64;
    
    public ColorRGB8(short red, short green, short blue)
    {
        Red = red;
        Green = green;
        Blue = blue;
    }
    /**
     * Creates an RGB color with 8 bits per pixel color depth.
     * @param argb 
     */
    public ColorRGB8(int argb)
    {
        // TODO: handle alpha channel
        Red = (short)((argb >> 16) & 0xFF);
        Green = (short)((argb >> 8) & 0xFF);
        Blue = (short)(argb & 0xFF);
    }
    public void Encode64()
    {
        Encode64(Base64.getEncoder());
    }
    public void Encode64(Base64.Encoder encoder)
    {
        _Encoded64 = encoder.encodeToString(new byte[]{
            (byte)Red,
            (byte)Green,
            (byte)Blue,
        });
    }
    public void SetDecoded64(String b64)
    {
        Base64.Decoder d = Base64.getDecoder();
        byte[] decoded = d.decode(b64);
        Red = decoded[0];
        Green = decoded[1];
        Blue = decoded[2];
        _Encoded64 = b64;
    }
    public String GetEncoded64(Base64.Encoder encoder)
    {
        if(_Encoded64 == null)
            Encode64(encoder);
        return _Encoded64;
    }
    public static ColorRGB8 Difference(ColorRGB8 a, ColorRGB8 b)
    {
        return new ColorRGB8(
                (short)(b.Red - a.Red),
                (short)(b.Green - a.Green),
                (short)(b.Blue - a.Blue));
    }
}
