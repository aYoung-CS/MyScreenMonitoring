package dbcon;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesEnc {

    /**
     * AES 对称加密（RSA非对称加密）
     * CBC 有向量 （ECB 无向量）
     * PKCS5Padding 填充模式（NoPadding 无填充）
     */
    private static final String ALG_AES_CBC_PKCS5 = "AES/CBC/PKCS5Padding";

    private static final String ALGORITHM = "AES";

    private static final Charset UTF8 = StandardCharsets.UTF_8;

    private String aesKey = "12e476beac1a4g20";  // 指定好的秘钥，非Base64和16进制

    private String aesIv = "2e119e58a526bc64";   // 偏移量

    private SecretKeySpec skeySpec;

    private IvParameterSpec iv;

    /**
     * 解密方法
     * @param cipherStr Base64编码的加密字节数组形式
     * @return 解密后的字节
     * @throws Exception 异常
     */
    public byte[] decrypt(byte[] cipherStr) throws Exception{
        // step 1 获得一个密码器
        Cipher cipher = Cipher.getInstance(ALG_AES_CBC_PKCS5);
        // step 2 初始化密码器，指定是加密还是解密(Cipher.DECRYPT_MODE 解密; Cipher.ENCRYPT_MODE 加密)
        // 加密时使用的盐来够造秘钥对象
        skeySpec = new SecretKeySpec(aesKey.getBytes(),ALGORITHM);
        // 加密时使用的向量，16位字符串
        iv = new IvParameterSpec(aesIv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        // 对加密报文进行base64解码
        byte[] encrypted1 = Base64.getDecoder().decode(cipherStr);
        // 解密后的报文数组
        byte[] original = cipher.doFinal(encrypted1);
        // 输出utf8编码的字符串，输出字符串需要指定编码格式
        return original;
    }

    /**
     * 加密
     * @param m 明文
     * @return Base64编码的密文
     * @throws Exception  加密异常
     */
    public byte[] encrypt(byte[] m) throws Exception{
        Cipher cipher = Cipher.getInstance(ALG_AES_CBC_PKCS5);
        skeySpec = new SecretKeySpec(aesKey.getBytes(),ALGORITHM);
        iv = new IvParameterSpec(aesIv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec,iv);
        // 这里的编码格式需要与解密编码一致
        byte[] encryptText = cipher.doFinal(m);
        return Base64.getEncoder().encode(encryptText);
    }

//    public static void main(String[] args) {
//        AesEnc testOne = new AesEnc();
//        byte[] plainText = "明文报文，进行对称AES算法加密传输".getBytes(StandardCharsets.UTF_8);
//        String cipherStr;
//        try {
//            System.out.println("被加解密的报文:[ " + new String(plainText,StandardCharsets.UTF_8) + " ]");
//            cipherStr = testOne.encrypt(plainText);
//            System.out.println("AES 加密后的Base64报文:[ " + cipherStr + "]");
//            System.out.println("对加密后的报文解密后的明文为:[ " + new String(testOne.decrypt(cipherStr),StandardCharsets.UTF_8) + " ]");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}

