package search;

public class BinarySearch {

    // Pred: args.length > 0 && for all i:[0..args.length-1] isDigit(args[i]) && for all i in [1..args.length-1] a[i] >= a[i + 1]
    // Post: R: a[R - 1] > x >= a[R] && 0 <= R <= args.length - 1
    public static void main(String[] args) {
        int[] numb = new int[args.length - 1];        
        int x = Integer.parseInt(args[0]);
        for (int i = 1; i < args.length; i++) {
            numb[i - 1] = Integer.parseInt(args[i]);
        }
        // numb != null
        System.out.println(iterativeBinarySearch(numb, 0, numb.length, x));
        // System.out.println(recursiveBinarySearch(numb, 0, numb.length, x));
    }

    // Pred: a != null && 0 <= l <= r <= a.length && for all i:[l..r - 1] a[i] >= a[i + 1]
    // Post: R: a[R - 1] > x >= a[R] && l <= R <= r
    public static int iterativeBinarySearch(int a[], int l, int r, int x) {
        int i = l - 1, j = r;
        // Invariant(I): l <= i + 1 <= j <= r && a[i] > x >= a[j]
        while (i + 1 < j) {
            // I && i + 1 < j
            int m = i + (j - i) / 2;
            // BinFunc(): I && i < m < j
            if (a[m] <= x) {
                // BinFunc() && a[i] > x >= a[m] >= a[j] 
                j = m;
                // BinFunc() && a[i] > x >= a[j'] && j' == m && j > j' >= i + 1
            } else {
                // BinFunc() && a[i] >= a[m] > x >= a[j] 
                i = m;
                // BinFunc() && a[i'] > x >= a[j] && i' == m && i < i' <= j - 1
            }
            // BinFunc() && a[i'] > x >= a[j'] && i' + 1 <= j'
        }
        // I && i + 1 >= j -->
        // I && i + 1 == j
        return j;
    }

    // immutable(a) : a' == a
    // Pred: a != null && 0 <= l <= r <= a.length && for all i:[l..r - 1] a[i] >= a[i + 1]
    // Post: R: a[R - 1] > x >= a[R] && l <= R <= r
    // Invariant(I): immutable(a) && 0 <= l <= r <= a.length && a[l - 1] > x >= a[r]
    public int recursiveBinarySearch(int[] a, int l, int r, int x) {
        // I
        if (l < r) {
            // I && l < r
            int m = l + (r - l) / 2;
            // Func(): I && l <= m < r && l < m + 1
            if (a[m] <= x) {
                // Func() && a[l - 1] > x >= a[m] >= a[r] && r' == m && l <= r' < r
                return recursiveBinarySearch(a, l, m, x);
            } else {
                // Func() && a[l - 1] > a[m] > x >= a[r] && l' == m + 1 && l < l' <= r
                return recursiveBinarySearch(a, m + 1, r, x);
            }
            // I && Post
        } else {
            // I && l >= r -->
            // I && l == r
            return r;
        }
    }
}
