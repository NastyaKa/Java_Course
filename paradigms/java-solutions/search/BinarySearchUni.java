package search;

public class BinarySearchUni {

    // Ex(l, r): exists m : a[l] > ... > a[m] <= a[m + 1] < ... < a[r]

    // Pred: args.length > 0 && for all i:[0..args.length-1] isDigit(args[i]) && 
    //       && Ex(0, args.length-1)
    
    // Post: R: a[0] > ... > a[R] <= a[R + 1] < ... < a[args.length - 1] && 0 <= R <= args.length - 1

    public static void main(String[] args) {
        int[] numb = new int[args.length];        
        for (int i = 0; i < args.length; i++) {
            numb[i] = Integer.parseInt(args[i]);
        }
        // numb != null
        System.out.println(recursiveBinarySearchUni(numb, 0, numb.length));
        // System.out.println(iterativeBinarySearchUni(numb, 0, numb.length));
    }

    // immutable(a) : a' == a
    // Pred: a != null && 0 <= l <= r <= a.length && Ex(l, r)
    // Post: R: a[R - 1] > x >= a[R] && l <= R <= r
    // Invariant(I): immutable(a) && 0 <= l <= r <= a.length && Ex(l, r)
    public static int recursiveBinarySearchUni(int[] a, int l, int r) {
        if (l + 1 < r) {
            // I && l + 1 < r
            int m = l + (r - l) / 2;
            // BinFunc(): I && l < m < r
            if (a[m - 1] >= a[m]) {
                // BinFunc() && a[m - 1] >= a[m] && l' == m && l < l' <= r - 1 && Ex(l', r)
                return recursiveBinarySearchUni(a, m, r);
            } else {
                // BinFunc() && a[m - 1] < a[m] && r' == m && l + 1 <= r' < r && Ex(l, r')
                return recursiveBinarySearchUni(a, l, m);
            }
            // I && Post
        } else {
            // I && l + 1 >= r -->
            // I && l + 1 == r
            return l;
        }
    }

    // Pred: a != null && 0 <= l <= r <= a.length && Ex(l, r)
    // Post: R: a[R - 1] > x >= a[R] && l <= R <= r
    public static int iterativeBinarySearchUni(int[] a, int l, int r) {
        int i = l, j = r;
        // Invariant(I): l <= i + 1 <= j <= r && Ex(i, j)
        while (i + 1 < j) {
            // I && i + 1 < j
            int m = i + (j - i) / 2;
            // BinFunc(): I && i < m < j
            if (a[m - 1] >= a[m]) {
                // BinFunc() && a[m - 1] >= a[m]
                i = m;
                // BinFunc() && i' == m && i < i' <= j - 1 && a[i' - 1] >= a[i'] && Ex(i', j)
            } else {
                // BinFunc() && a[m - 1] < a[m]
                j = m;
                // BinFunc() && j' == m && i + 1 <= j' < j && a[j' - 1] < a[j'] && Ex(i, j')
            }
            // BinFunc() && i' + 1 <= j' && Ex(i', j')
        }
        // I && i + 1 >= j -->
        // I && i + 1 == j
        return i;
    }
}
