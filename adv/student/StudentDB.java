package info.kgeorgiy.ja.kaimakova.student;

import info.kgeorgiy.java.advanced.student.*;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StudentDB implements AdvancedQuery {

    public static final Comparator<Student> STUDENT_COMPARATOR = Comparator.comparing(Student::getLastName, Comparator.reverseOrder())
            .thenComparing(Student::getFirstName, Comparator.reverseOrder())
            .thenComparingInt(Student::getId);

    private String getFullName(final Student student) {
        return student.getFirstName() + " " + student.getLastName();
    }

    private <C> Stream<C> getByField(final List<Student> students, final Function<Student, C> mupFunc) {
        return students.stream().map(mupFunc);
    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return getByField(students, Student::getFirstName).toList();
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return getByField(students, Student::getLastName).toList();
    }

    @Override
    public List<GroupName> getGroups(List<Student> students) {
        return getByField(students, Student::getGroup).toList();
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return getByField(students, this::getFullName).toList();
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return getByField(students, Student::getFirstName)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private List<Student> sortStudentsBy(final Collection<Student> students, final Comparator<Student> comparator) {
        return students.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return sortStudentsBy(students, Comparator.naturalOrder());
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return sortStudentsBy(students, STUDENT_COMPARATOR);
    }

    private <C> Function<Student, Boolean> filterCreator(final Function<Student, C> getFunc, final C toCompare) {
        return student -> getFunc.apply(student).equals(toCompare);
    }

    private <C> Stream<Student> findStudentsByFunc(final Collection<Student> students,
                                                   final Function<Student, C> getFunc, final C toCompare) {
        return students.stream()
                .filter(filterCreator(getFunc, toCompare)::apply);
    }

    private List<Student> makeSorted(Stream<Student> stream) {
        return stream.sorted(STUDENT_COMPARATOR).toList();
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return makeSorted(findStudentsByFunc(students, Student::getFirstName, name));
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return makeSorted(findStudentsByFunc(students, Student::getLastName, name));
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName group) {
        return makeSorted(findStudentsByFunc(students, Student::getGroup, group));
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName group) {
        return findStudentsByFunc(students, Student::getGroup, group)
                .collect(Collectors.toMap(
                        Student::getLastName,
                        Student::getFirstName,
                        BinaryOperator.minBy(Comparator.naturalOrder())));
    }

    private <K, A, D> Stream<Map.Entry<K, D>> streamByGroupingCollector(final Collection<Student> students,
                                                                        final Function<Student, K> classifier,
                                                                        final Collector<Student, A, D> downstream) {
        return students.stream()
                .collect(Collectors.groupingBy(classifier, downstream))
                .entrySet().stream();
    }

    private List<Group> getGroupsBy(final Collection<Student> students,
                                    final Function<List<Student>, List<Student>> valueMapper
    ) {
        return streamByGroupingCollector(students, Student::getGroup, Collectors.toList())
                .map(g -> new Group(g.getKey(), valueMapper.apply(g.getValue())))
                .sorted(Comparator.comparing(Group::getName))
                .toList();
    }

    @Override
    public List<Group> getGroupsByName(Collection<Student> students) {
        return getGroupsBy(students, this::sortStudentsByName);
    }

    @Override
    public List<Group> getGroupsById(Collection<Student> students) {
        return getGroupsBy(students, this::sortStudentsById);
    }

    private <C, K> K getMaxBy(Stream<C> stream, final Comparator<C> comparator, final Function<C, K> mupFunc, K def) {
        return stream.max(comparator).map(mupFunc).orElse(def);
    }

    @Override
    public String getMaxStudentFirstName(List<Student> students) {
        return getMaxBy(students.stream(), Comparator.naturalOrder(), Student::getFirstName, "");
    }

    private <F, C, D extends Comparable<D>> C getLargestBy(Collection<Student> students,
                                                           Function<Student, C> grouper,
                                                           Collector<Student, ?, F> collector,
                                                           Function<Map.Entry<C, F>, D> firstCompare,
                                                           Comparator<C> order, C def) {
        return getMaxBy(streamByGroupingCollector(students, grouper, collector),
                Comparator.comparing(firstCompare).thenComparing(Map.Entry.comparingByKey(order)),
                Map.Entry::getKey,
                def);
    }

    @Override
    public GroupName getLargestGroup(Collection<Student> students) {
        return getLargestBy(students,
                Student::getGroup,
                Collectors.counting(),
                Map.Entry::getValue,
                Comparator.naturalOrder(),
                null);
    }

    @Override
    public GroupName getLargestGroupFirstName(Collection<Student> students) {
        return getLargestBy(students,
                Student::getGroup,
                Collectors.mapping(Student::getFirstName, Collectors.toSet()),
                (Map.Entry<GroupName, Set<String>> entry) -> entry.getValue().size(),
                Comparator.reverseOrder(),
                null);
    }

    @Override
    public String getMostPopularName(Collection<Student> students) {
        return getLargestBy(students,
                Student::getFirstName,
                Collectors.mapping(Student::getGroup, Collectors.toSet()),
                (Map.Entry<String, Set<GroupName>> entry) -> entry.getValue().size(),
                Comparator.reverseOrder(),
                "");
    }

    private Student filterAny(final Collection<Student> students,
                              final Function<Student, Boolean> filterFunc) {
        return students.stream()
                .filter(filterFunc::apply).findFirst().orElse(null);
    }

    private <C> List<C> getByField(final Collection<Student> students,
                                   final int[] ids, final Function<Student, C> mupFunc) {
        return Arrays.stream(ids)
                .mapToObj(i -> filterAny(students, filterCreator(Student::getId, i)))
                .map(mupFunc)
                .toList();
    }

    @Override
    public List<String> getFirstNames(Collection<Student> students, int[] ids) {
        return getByField(students, ids, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(Collection<Student> students, int[] ids) {
        return getByField(students, ids, Student::getLastName);
    }

    @Override
    public List<GroupName> getGroups(Collection<Student> students, int[] ids) {
        return getByField(students, ids, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(Collection<Student> students, int[] ids) {
        return getByField(students, ids, this::getFullName);
    }
}
