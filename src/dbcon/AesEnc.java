package dbcon;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesEnc {

    /**
     * AES �ԳƼ��ܣ�RSA�ǶԳƼ��ܣ�
     * CBC ������ ��ECB ��������
     * PKCS5Padding ���ģʽ��NoPadding ����䣩
     */
    private static final String ALG_AES_CBC_PKCS5 = "AES/CBC/PKCS5Padding";

    private static final String ALGORITHM = "AES";

    private static final Charset UTF8 = StandardCharsets.UTF_8;

    private String aesKey = "12e476beac1a4g20";  // ָ���õ���Կ����Base64��16����

    private String aesIv = "2e119e58a526bc64";   // ƫ����

    private SecretKeySpec skeySpec;

    private IvParameterSpec iv;

    /**
     * ���ܷ���
     * @param cipherStr Base64����ļ����ֽ�������ʽ
     * @return ���ܺ���ֽ�
     * @throws Exception �쳣
     */
    public byte[] decrypt(byte[] cipherStr) throws Exception{
        // step 1 ���һ��������
        Cipher cipher = Cipher.getInstance(ALG_AES_CBC_PKCS5);
        // step 2 ��ʼ����������ָ���Ǽ��ܻ��ǽ���(Cipher.DECRYPT_MODE ����; Cipher.ENCRYPT_MODE ����)
        // ����ʱʹ�õ�����������Կ����
        skeySpec = new SecretKeySpec(aesKey.getBytes(),ALGORITHM);
        // ����ʱʹ�õ�������16λ�ַ���
        iv = new IvParameterSpec(aesIv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        // �Լ��ܱ��Ľ���base64����
        byte[] encrypted1 = Base64.getDecoder().decode(cipherStr);
        // ���ܺ�ı�������
        byte[] original = cipher.doFinal(encrypted1);
        // ���utf8������ַ���������ַ�����Ҫָ�������ʽ
        return original;
    }

    /**
     * ����
     * @param m ����
     * @return Base64���������
     * @throws Exception  �����쳣
     */
    public byte[] encrypt(byte[] m) throws Exception{
        Cipher cipher = Cipher.getInstance(ALG_AES_CBC_PKCS5);
        skeySpec = new SecretKeySpec(aesKey.getBytes(),ALGORITHM);
        iv = new IvParameterSpec(aesIv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec,iv);
        // ����ı����ʽ��Ҫ����ܱ���һ��
        byte[] encryptText = cipher.doFinal(m);
        return Base64.getEncoder().encode(encryptText);
    }

//    public static void main(String[] args) {
//        AesEnc testOne = new AesEnc();
//        byte[] plainText = "���ı��ģ����жԳ�AES�㷨���ܴ���".getBytes(StandardCharsets.UTF_8);
//        String cipherStr;
//        try {
//            System.out.println("���ӽ��ܵı���:[ " + new String(plainText,StandardCharsets.UTF_8) + " ]");
//            cipherStr = testOne.encrypt(plainText);
//            System.out.println("AES ���ܺ��Base64����:[ " + cipherStr + "]");
//            System.out.println("�Լ��ܺ�ı��Ľ��ܺ������Ϊ:[ " + new String(testOne.decrypt(cipherStr),StandardCharsets.UTF_8) + " ]");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}

