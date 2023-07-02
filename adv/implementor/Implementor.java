package info.kgeorgiy.ja.kaimakova.implementor;

import info.kgeorgiy.java.advanced.implementor.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Classes implementor. Generates implementations of classes and interfaces.
 *
 * @author Anastasiia Kaimakova
 * @see info.kgeorgiy.java.advanced.implementor.Impler
 */
public class Implementor implements Impler {

    /**
     * The system-dependent default name-separator character.
     */
    private static final char FILE_SPR = File.separatorChar;

    /**
     * The system-dependent line separator string.
     */
    private static final String LINE_SERP = System.lineSeparator();

    /**
     * Tabulation string.
     */
    private static final String TAB = "    ";

    /**
     * Suffix for naming of implementation.
     */
    private static final String IMPL = "Impl";

    /**
     * Java file extension.
     */
    private static final String JAVA = ".java";

    /**
     * Main method for running from console.
     * Parameters (no one allowed to be null):
     * <ol>
     * <li>
     *     <code>Class name</code> name of the implementing class
     * </li>
     * <li>
     *     <code>Path</code> path to the implementing Class
     * </li>
     * </ol>
     *
     * @param args should be entered to command line and should be non-null
     */
    public static void main(final String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Error. Expected non null: <Class name> <Path>");
        } else {
            try {
                new Implementor().implement(Class.forName(args[0]), Path.of(args[1]));
            } catch (ClassNotFoundException e) {
                System.err.println("Class " + args[args.length - 2] + " not found: ");
            } catch (InvalidPathException e) {
                System.err.println("Invalid path: " + args[args.length - 1]);
            } catch (ImplerException e) {
                System.err.println("Implementation error: " + e.getMessage());
            }
        }
    }

    /**
     * Returns {@code String} shifted right with {@value TAB} {@code count} times.
     *
     * @param paragraph {@code String} for tabulation
     * @param count     the repeat number
     * @return tabulated {@code String}
     */
    private static String makeIndent(final String paragraph, final int count) {
        return TAB.repeat(count).concat(paragraph);
    }

    /**
     * Checks if {@code Class} class can't be implemented.
     * Can't be implemented if {@code Class}:
     *
     * <ul>
     *     <li>Has final or private modifier</li>
     *     <li>Primitive</li>
     *     <li>Enum</li>
     *     <li>Array</li>
     * </ul>
     *
     * @param token given class
     * @return {@code true} if impossible to implement
     */
    private static boolean notImplementedType(final Class<?> token) {
        final int modifier = token.getModifiers();
        return Modifier.isFinal(modifier)
                || Modifier.isPrivate(modifier)
                || token.isPrimitive()
                || token.equals(Enum.class)
                || token.isArray();
    }

    /**
     * Creates parent directories, if they weren't present
     *
     * @param root {@code Path} to file or directory
     */
    private static Path createDirectories(final Path root) {
        final Path parentDir = root.getParent();
        if (parentDir != null) {
            try {
                Files.createDirectories(parentDir);
            } catch (IOException e) {
                // ignored
            }
        }
        return root;
    }

    /**
     * Returns the line with "package " + {@code Class's} package name.
     * If no package, then returns empty {@code String}.
     *
     * @param token given class.
     * @return {@code String} with information about package.
     * @see Class#getPackageName()
     */
    private static String getPackageLine(final Class<?> token) {
        final String pckg = token.getPackageName();
        return pckg.isEmpty() ? "" : "package " + pckg + ";";
    }

    /**
     * Creates Class's simple name + {@value IMPL} suffix
     *
     * @param token given class
     * @return {@code String} for {@code Class}
     * @see Class#getSimpleName()
     */
    private static String getClassImplName(final Class<?> token) {
        return token.getSimpleName().concat(IMPL);
    }

    /**
     * Resolve the given path with implementing file with {@value JAVA} suffix.
     *
     * @param token given {@code CLass} for implementing
     * @param root  given {@code Path}
     * @return the resulting path
     */
    private static Path getClassImplPath(final Class<?> token, final Path root) {
        return root.resolve(Path.of(token.getPackageName().replace('.', FILE_SPR))
                .resolve(getClassImplName(token).concat(JAVA)));
    }

    /**
     * Creates class declaration.
     * Format is:
     * "public class " + {@link Implementor#getClassImplName(Class)} " implements " + token's canonical name
     * if {@code token} represents an interface type, otherwise:
     * "public class " + {@link Implementor#getClassImplName(Class)} " extends " + token's canonical name
     *
     * @param token given class
     * @return the resulting string
     */
    private static String getClassDeclaration(final Class<?> token) {
        return String.format(" public class %s %s %s {",
                getClassImplName(token),
                (token.isInterface() ? "implements" : "extends"),
                token.getCanonicalName()
        );
    }

    /**
     * Collects items from array to {@code String} with comma + space delimiter.
     *
     * @param array  array of items to collect
     * @param mapper mapping to {@code String} function
     * @param <C>    type of given array
     * @return {@code String}, collected from array's mapped items
     */
    private static <C> String collectFromArray(final C[] array, final Function<C, String> mapper) {
        return Arrays.stream(array)
                .map(mapper)
                .collect(Collectors.joining(", "));
    }

    /**
     * Returns enumeration of params.
     * Each parameter represents with its type and its name.
     *
     * @param params array of parameters from an implemented method or a constructor
     * @return formatted code of arguments
     */
    private static String getArgs(final Parameter[] params) {
        return collectFromArray(params, param -> String.format("%s %s",
                param.getType().getCanonicalName(),
                param.getName())
        );
    }

    /**
     * Returns enumeration of exceptions.
     * If array {@code e} is empty, then returns empty string.
     * Otherwise, "throw " + each exception represents with its canonical name.
     *
     * @param e array of exception's classes
     * @return formatted code of throwable exceptions
     */
    private static String getExceptions(final Class<?>[] e) {
        return e.length == 0 ? "" : "throws ".concat(collectFromArray(e, Class::getCanonicalName));
    }

    /**
     * Creates signatures for {@code Executable} method/constructor
     * Format is:
     * "public " + {@code name} + + "(" + {@link Implementor#getArgs(Parameter[])}
     * + ") " + {@link Implementor#getExceptions(Class[])} + " {"
     *
     * @param name       name of method/constructor
     * @param executable method/constructor
     * @return formatted code for signature of {@code Executable} method/constructor
     */
    private static String getExecutableSignature(final String name, final Executable executable) {
        return String.format("public %s(%s) %s {",
                name,
                getArgs(executable.getParameters()),
                getExceptions(executable.getExceptionTypes())
        );
    }

    /**
     * Creates code for constructor's body.
     * Format is:
     * "super(" + enumeration of constructor's parameters + ")"
     *
     * @param constructor implementing constructor
     * @return {@code String} formatted body of {@code Constructor}
     */
    private static String getConstructorBody(final Constructor<?> constructor) {
        return String.format("super(%s);", collectFromArray(constructor.getParameters(), Parameter::getName));
    }

    /**
     * Creates code for constructor.
     * If {@code token} epresents an interface type, returns empty string.
     * If any constructor is private, then throws {@code ImplerException}.
     * Otherwise, returns token's signature + tabulated {@link Implementor#getConstructorBody(Constructor)} + "{",
     * delimited by {@link Implementor#LINE_SERP}
     *
     * @param token given class
     * @return {@code String} formatted code for token's constructor
     * @throws ImplerException if {@code Class} has no non-private {@code Constructor}
     */
    private static String getClassConstructor(final Class<?> token) throws ImplerException {
        if (token.isInterface()) {
            return "";
        }
        final Constructor<?> constructor = Arrays.stream(token.getDeclaredConstructors())
                .filter(c -> !Modifier.isPrivate(c.getModifiers())).findFirst()
                .orElse(null);
        if (Objects.isNull(constructor)) {
            throw new ImplerException("Class has no constructors for implementation");
        }
        return String.join(
                LINE_SERP,
                makeIndent(getExecutableSignature(getClassImplName(token), constructor), 1),
                makeIndent(getConstructorBody(constructor), 2),
                makeIndent("}", 1)
        );
    }

    /**
     * Creates default return code for {@code Class}
     * <ul>
     *      <li>"" for {@code void}</li>
     *      <li>"{@code true}" for {@code boolean}</li>
     *      <li>"{@code null}" for {@code Object}</li>
     *      <li>"{@code 0}" for primitive type</li>
     * </ul>
     *
     * @param token given class
     * @return return formatted code for {@code Class} implementation
     */
    private static String getDefaultForReturn(final Class<?> token) {
        return token.isPrimitive() ? (token == void.class ? "" : (token == boolean.class ? "true" : "0")) : "null";
    }

    /**
     * Creates code for method's body.
     * Format is:
     * "return " + method's default return (by {@link Implementor#getDefaultForReturn(Class)}) + ";".
     *
     * @param method implementing {@code Method}
     * @return {@code String} formatted body code for {@code Method}
     */
    private static String getMethodBody(final Method method) {
        return String.format("return %s;", getDefaultForReturn(method.getReturnType()));
    }

    /**
     * Creates signatures for {@code Method}
     * Collects by {@link Implementor#getExecutableSignature(String, Executable)}.
     * Names is {@code method's} type canonical name + " " + {@code method's} name.
     *
     * @param method implementing method
     * @return formatted code for signature of {@code Method}
     * @see Implementor#getExecutableSignature(String, Executable)
     */
    private static String getMethodSignature(final Method method) {
        return getExecutableSignature(String.format("%s %s",
                        method.getReturnType().getCanonicalName(),
                        method.getName()),
                method);
    }

    /**
     * Creates {@code Method} implementation.
     * Contains {@link Implementor#getMethodSignature(Method)},
     * {@link Implementor#getMethodBody(Method)}.
     * Joins it to be a compilable and formatted code.
     *
     * @param method implementing method
     * @return formatted code for {@code Method}
     */
    private static String getMethodImpl(final Method method) {
        return String.join(
                LINE_SERP,
                makeIndent(getMethodSignature(method), 1),
                makeIndent(getMethodBody(method), 2),
                makeIndent("}", 1)
        );
    }

    /**
     * Adds all items from {@code arr} to given {@code Map<> methods}.
     * Each item merged into given {@code Map<>} methods.
     * The {@code remappingFunction} for merge replace old value to new one, if old value has return type, witch
     * assignable from new value's one.
     *
     * @param arr     array with values to be merged into {@code methods}
     * @param methods map of existing values
     */
    private static void addToMapFromArray(final Method[] arr, final Map<MethodHelper, Method> methods) {
        Arrays.stream(arr)
                .forEach(method -> methods.merge(new MethodHelper(method),
                        method, (a, b) -> a.getReturnType().isAssignableFrom(b.getReturnType()) ? b : a));
    }

    /**
     * Creates implementation for all {@code token's} and {@code token's} super classes' abstract methods.
     *
     * @param token given class
     * @return implementation for all distinct abstract and non-final methods
     */
    private static String getClassMethods(final Class<?> token) throws ImplerException {
        final Map<MethodHelper, Method> methods = new HashMap<>();
        for (Class<?> it = token; it != null; it = it.getSuperclass()) {
            addToMapFromArray(it.getMethods(), methods);
            addToMapFromArray(it.getDeclaredMethods(), methods);
        }
        if (methods.entrySet().stream().anyMatch(method ->
                Modifier.isAbstract(method.getValue().getModifiers()) && (
                        Arrays.stream(method.getValue().getParameterTypes())
                                .anyMatch(param -> Modifier.isPrivate(param.getModifiers()))
                                || Modifier.isPrivate(method.getValue().getReturnType().getModifiers())))) {
            throw new ImplerException("This class cannot be implemented");
        }
        return methods.entrySet().stream()
                .filter(method -> Modifier.isAbstract(method.getKey().method.getModifiers()))
                .map(method -> getMethodImpl(method.getValue()))
                .collect(Collectors.joining(LINE_SERP));
    }

    /**
     * Creates formatted implementation for {@code token}.
     * Format is:
     * <br>
     * {@link Implementor#getPackageLine(Class)} +
     * <br>
     * {@link Implementor#getClassDeclaration(Class)} +
     * <br>
     * {@link Implementor#getClassConstructor(Class)} +
     * <br>
     * {@link Implementor#getClassMethods(Class)} +
     * <br>
     * "}"
     *
     * @param token given class
     * @return implemented code for {@code token}
     * @throws ImplerException if {@code Class} has no available constructors.
     */
    private static String classImplementation(final Class<?> token) throws ImplerException {
        return String.join(
                LINE_SERP,
                getPackageLine(token),
                getClassDeclaration(token),
                getClassConstructor(token),
                getClassMethods(token),
                "}"
        );
    }

    /**
     * Implementation of {@link Impler#implement(Class, Path)}.
     * Creates directories to {@code root} to save implemented token.
     * Creates {@link BufferedWriter} to write {@link Implementor#classImplementation(Class)} result to file.
     *
     * @param token given class
     * @param root  path to file
     * @throws ImplerException if {@code Class} cannot be implemented
     * @see Implementor#notImplementedType(Class)
     */
    @Override
    public void implement(final Class<?> token, Path root) throws ImplerException {
        if (notImplementedType(token)) {
            throw new ImplerException("This class cannot be implemented");
        }

        try (BufferedWriter writer =
                     Files.newBufferedWriter(createDirectories(getClassImplPath(token, root)), StandardCharsets.UTF_8)) {
            writer.write(classImplementation(token));
        } catch (IOException e) {
            throw new ImplerException("IOException caused by BufferedWriter: ", e);
        }
    }

    /**
     * Class wrapper for comparing methods by signature
     */
    private record MethodHelper(Method method) {

        /**
         * Custom hashCode realization
         *
         * @return hash based on method's name hashCode and method's ParameterTypes hashCode
         */
        @Override
        public int hashCode() {
            return Objects.hash(method.getName().hashCode(),
                    Arrays.hashCode(method.getParameterTypes()));
        }

        /**
         * Custom equals realization
         *
         * @param obj the reference object with which to compare.
         * @return {@code true} if methods' signatures are equals or links matches
         */
        @Override
        public boolean equals(final Object obj) {
            if (method == obj) {
                return true;
            } else if (!(obj instanceof MethodHelper)) {
                return false;
            }
            final Method toCompare = ((MethodHelper) obj).method;
            return method.getName().equals(toCompare.getName())
                    && Arrays.equals(method.getParameterTypes(), toCompare.getParameterTypes());
        }
    }
}