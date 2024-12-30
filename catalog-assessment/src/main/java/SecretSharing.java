package src.main.java;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class SecretSharing {
    public static void main(String[] args) {
        try {
            System.out.println("Running for input1.json");
            run("input1.json");
            System.out.println("Running for input2.json");
            run("input2.json");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void run(String fileName) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(fileName));

            // Extract keys
            int n = rootNode.get("keys").get("n").asInt();
            int k = rootNode.get("keys").get("k").asInt();

            // Read and decode the roots
            List<Point> points = new ArrayList<>();
            for (int i = 1; i <= n; i++) {
                String x = Integer.toString(i);
                if (rootNode.has(x)) {
                    int base = rootNode.get(x).get("base").asInt();
                    String value = rootNode.get(x).get("value").asText();
                    BigInteger y = new BigInteger(value, base);
                    points.add(new Point(new BigInteger(x), y));
                }
            }

            // Calculate the constant term using Lagrange Interpolation
            BigInteger secret = lagrangeInterpolation(points, BigInteger.ZERO, k);
            System.out.println("The secret constant term (c) for " + fileName + " is: " + secret);
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }
    }

    // Lagrange interpolation to find the value of f(0)
    public static BigInteger lagrangeInterpolation(List<Point> points, BigInteger x, int k) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    numerator = numerator.multiply(x.subtract(points.get(j).x));
                    denominator = denominator.multiply(points.get(i).x.subtract(points.get(j).x));
                }
            }

            BigInteger term = points.get(i).y.multiply(numerator).divide(denominator);
            result = result.add(term);
        }

        return result;
    }

    // Point class to store (x, y) values
    static class Point {
        BigInteger x;
        BigInteger y;

        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
}
