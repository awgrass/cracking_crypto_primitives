package square.aes;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.*;

/**
 * Created by franconieddu on 15.06.18.
 */
public class Main {

    private static void printThreeRoundDistinguisher()
    {
        LambdaSet lambdaSet = new LambdaSet();
        SecureRandom randomGenerator = new SecureRandom();
        randomGenerator.setSeed(randomGenerator.generateSeed(16));
        byte[] keyBuffer = new byte[16];
        randomGenerator.nextBytes(keyBuffer);

        Data randomKey = new Data(keyBuffer);
        for (int i  = 0; i < 5; ++i)
        {
            System.out.println("=========");
            System.out.println("round " + i);
            System.out.println("=========");
            AES aes = new AES(randomKey, i);
            LambdaSet updatedSet = aes.encrypt(lambdaSet);

            System.out.println(updatedSet.getActiveConstantDist());
            System.out.println(updatedSet.getBalancedDist());
        }
    }

    private static List<Set<Byte>> getCandidates(LambdaSet set)
    {
        Data guessedKey = new Data(new byte[16]);
        List<Set<Byte>> retVal = new ArrayList<>();
        for (int row = 0; row < guessedKey.getNumRows(); ++row)
        {
            for (int col = 0; col < guessedKey.getNumCols(); ++col)
            {
                retVal.add(new HashSet<>());
                for (int j = 0; j < 256; ++j)
                {
                    guessedKey.byteMatrix[row][col] = (byte) j;
                    guessedKey.updateArray();
                    LambdaSet thirdRoundSet = AES.decryptOneRound(set, guessedKey, 4);
                    if (thirdRoundSet.isBalanced(row, ((col + row) % 4))) {
                        //balanced
                        retVal.get(retVal.size() - 1).add((byte) j);
                    }
                }
            }
        }
        return retVal;
    }

    private static boolean mergeSets(List<Set<Byte>> s1, List<Set<Byte>> s2)
    {
        if (s1.isEmpty())
        {
            s1.addAll(s2);
            return false;
        }
        boolean retVal = true;
        for (int i = 0; i < s1.size(); ++i)
        {
            s1.get(i).retainAll(s2.get(i));
            retVal &= s1.get(i).size() == 1;
        }
        return retVal;
    }

    public static void getKey()
    {
        SecureRandom randomGenerator = new SecureRandom();
        randomGenerator.setSeed(randomGenerator.generateSeed(16));
        byte[] keyBuffer = new byte[16];
        randomGenerator.nextBytes(keyBuffer);

        AES aes = new AES(new Data(keyBuffer), 4);
        int counter = 0;
        List<Set<Byte>> deducedKeySet = new ArrayList<>();
        List<Set<Byte>> candidateSet;
        do
        {
            LambdaSet set = aes.encrypt(new LambdaSet((byte) counter));
            candidateSet = getCandidates(set);
            counter++;
        } while(!mergeSets(deducedKeySet, candidateSet));

        Data deducedKeyState = new Data(new byte[16]);
        for (int row = 0; row < 4; ++row)
        {
            for (int col = 0; col < 4;++col)
            {
                for (Byte currentByte : deducedKeySet.get((row * 4) + col))
                    deducedKeyState.byteMatrix[row][col] = currentByte;
            }
        }
        deducedKeyState.updateArray();
        //Data test2 = new Data(rawKeyBytes);
        //AES.mixColumns(deducedKeyState, true);
        System.out.println(aes.getRoundKey(4));
        System.out.println(deducedKeyState);
    }

    public static void main(String[] args)
    {
        /*Data randomKey = new Data("1234567890123456".getBytes());
        AES test = new AES(randomKey, 3);
        System.out.println(test.encrypt("1234567890123456".getBytes()));
        AES aes = new AES(randomKey, 4);
        Data cipherText = aes.encrypt("1234567890123456".getBytes()); //u2rTFYYeR4MFpCCKOFRF5w==
        Data plainText = aes.decryptOneRound(cipherText.byteArray, aes.getRoundKey(4), 4); //wL67zQCOQwBpiSrX++xy0g==
        System.out.println(cipherText);
        System.out.println(plainText);*/
        //printThreeRoundDistinguisher();
        getKey();
    }
}
