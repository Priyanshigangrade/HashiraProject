import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class LagrangeInterpolation {
    
    // Parse a number string with given base
    private static BigInteger parseWithBase(String value, int base) {
        return new BigInteger(value, base);
    }
    
    // Lagrange interpolation to find constant term (c)
    private static BigInteger lagrangeInterpolation(List<BigInteger> xValues, List<BigInteger> yValues) {
        int n = xValues.size();
        BigInteger result = BigInteger.ZERO;
        
        for (int i = 0; i < n; i++) {
            BigInteger term = yValues.get(i);
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;
            
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    // numerator *= (0 - x_j)
                    numerator = numerator.multiply(BigInteger.ZERO.subtract(xValues.get(j)));
                    // denominator *= (x_i - x_j)
                    denominator = denominator.multiply(xValues.get(i).subtract(xValues.get(j)));
                }
            }
            
            // term = y_i * (numerator / denominator)
            BigInteger fraction = numerator.divide(denominator);
            term = term.multiply(fraction);
            result = result.add(term);
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        try {
            // Read JSON from file
            BufferedReader reader = new BufferedReader(new FileReader("input.json"));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            reader.close();
            
            JSONObject jsonObject = new JSONObject(jsonContent.toString());
            
            // Get n and k
            JSONObject keys = jsonObject.getJSONObject("keys");
            int n = keys.getInt("n");
            int k = keys.getInt("k");
            
            // Extract points (x, y)
            List<BigInteger> xValues = new ArrayList<>();
            List<BigInteger> yValues = new ArrayList<>();
            
            for (int i = 1; i <= n; i++) {
                String key = String.valueOf(i);
                if (jsonObject.has(key)) {
                    JSONObject point = jsonObject.getJSONObject(key);
                    int base = Integer.parseInt(point.getString("base"));
                    String value = point.getString("value");
                    
                    // x is the key (1, 2, 3, ...)
                    BigInteger x = new BigInteger(key);
                    
                    // Decode y from given base
                    BigInteger y = parseWithBase(value, base);
                    
                    xValues.add(x);
                    yValues.add(y);
                    
                    System.out.println("Point " + i + ": x=" + x + ", y=" + y + " (decoded from base " + base + ")");
                }
            }
            
            // Use first k points for interpolation
            List<BigInteger> xSubset = xValues.subList(0, k);
            List<BigInteger> ySubset = yValues.subList(0, k);
            
            // Find constant term c using Lagrange interpolation at x=0
            BigInteger c = lagrangeInterpolation(xSubset, ySubset);
            
            System.out.println("\nUsing first " + k + " points for interpolation");
            System.out.println("Constant term c: " + c);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}