package square.aes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class LambdaSet {
    List<Data> internalSet_;

    public LambdaSet()
    {
        internalSet_ = new ArrayList<>();
        for (int i = 0; i < 256; ++i)
        {
            byte[] bytes = new byte[16];
            Arrays.fill(bytes, (byte) 0x0);
            bytes[0] = (byte) i;

            internalSet_.add(new Data(bytes));
        }
    }

    public LambdaSet(byte c)
    {
       internalSet_ = new ArrayList<>();
       for (int i = 0; i < 256; ++i)
       {
           byte[] bytes = new byte[16];
           Arrays.fill(bytes, (byte) c);
           bytes[0] = (byte) i;

           internalSet_.add(new Data(bytes));
       }
    }

    public LambdaSet(List <Data> list)
    {
        internalSet_ = list;
    }

    public String getActiveConstantDist()
    {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("\n---------\n");
        for (int i = 0; i < 4; ++i)
        {
            sb.append('|');
            for (int j = 0; j < 4; ++j)
            {
                int index = (j * 4) + i;
                if (isActive(index))
                {
                    sb.append('A');
                }
                else if (isConstant(index))
                {
                    sb.append('C');
                }
                else
                {
                    sb.append('U');
                }
                sb.append('|');
            }
            sb.append("\n---------\n");
        }

        return sb.toString();

    }

    public String getBalancedDist()
    {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("\n---------\n");
        byte[] reduced = new ArrayList<Data>(internalSet_).stream().reduce(Data::XOR).get().byteArray;
        for (int i = 0; i < 4; ++i)
        {
            sb.append('|');
            for (int j = 0; j < 4; ++j)
            {
                sb.append(reduced[(j * 4) + i] == 0 ? 'B' : 'U');
                sb.append('|');
            }
            sb.append("\n---------\n");
        }
        return sb.toString();
    }

    public boolean isBalanced(int row, int col)
    {
        return internalSet_.stream().mapToInt(d -> d.byteMatrix[row][col]).reduce((x, y) ->  x ^ y).getAsInt() == 0;
    }

    private boolean isActive(int index)
    {
        Set<Byte> byteSet = new HashSet<>();
        internalSet_.forEach(d -> byteSet.add(d.byteArray[index]));
        return byteSet.size() == 256;
    }

    private boolean isConstant(int index)
    {
        Set<Byte> byteSet = new HashSet<>();
        internalSet_.forEach(d -> byteSet.add(d.byteArray[index]));
        return byteSet.size() == 1;
    }
}
