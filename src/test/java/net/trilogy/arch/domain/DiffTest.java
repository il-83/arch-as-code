package net.trilogy.arch.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.trilogy.arch.domain.architectureUpdate.YamlTdd;
import net.trilogy.arch.domain.architectureUpdate.YamlTdd.TddId;
import net.trilogy.arch.domain.c4.C4Type;
import net.trilogy.arch.domain.diff.Diff;
import net.trilogy.arch.domain.diff.Diffable;
import net.trilogy.arch.domain.diff.DiffableWithRelatedTdds;
import org.junit.Test;

import java.util.HashMap;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

public class DiffTest {

    @Test(expected = IllegalArgumentException.class)
    public void cannotCreateIfBothStatesNull_1() {
        new Diff(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotCreateIfBothStatesNull_2() {
        new Diff(null, Set.of(), null, Set.of());
    }

    @Test
    public void shouldCalculateCreatedStatus() {
        final Diff diff = new Diff(
                null, Set.of(),
                new Thing("toBeCreated"), Set.of()
        );
        assertThat(diff.getStatus(), equalTo(Diff.Status.CREATED));
    }

    @Test
    public void shouldCalculateDeletedStatus() {
        final Diff diff = new Diff(
                new Thing("toBeDeleted"), Set.of(),
                null, Set.of()
        );
        assertThat(diff.getStatus(), equalTo(Diff.Status.DELETED));
    }

    @Test
    public void shouldCalculateUpdatedStatus() {
        final Diff diff = new Diff(
                new Thing("toBeUpdated"), Set.of(),
                new Thing("updated"), Set.of()
        );
        assertThat(diff.getStatus(), equalTo(Diff.Status.UPDATED));
    }

    @Test
    public void shouldCalculateNoUpdateStatus() {
        final Diff diff = new Diff(
                new Thing("noChange"), Set.of(),
                new Thing("noChange"), Set.of()
        );
        assertThat(diff.getStatus(), equalTo(Diff.Status.NO_UPDATE));
    }

    @Test
    public void shouldCalculatedChildrenUpdated() {
        final Diff diff = new Diff(
                new Thing("noUpdate"), Set.of(new Thing("a")),
                new Thing("noUpdate"), Set.of(new Thing("b"))
        );
        assertThat(diff.getStatus(), equalTo(Diff.Status.NO_UPDATE_BUT_CHILDREN_UPDATED));
    }

    @Test
    public void shouldNotCalculateChildrenIfChangedStatus() {
        final Diff created = new Diff(
                null, Set.of(new Thing("a")),
                new Thing("toBeCreated"), Set.of(new Thing("b"))
        );
        final Diff deleted = new Diff(
                new Thing("toBeDeleted"), Set.of(new Thing("a")),
                null, Set.of(new Thing("b"))
        );
        final Diff updated = new Diff(
                new Thing("toBeUpdated"), Set.of(new Thing("a")),
                new Thing("updated"), Set.of(new Thing("b"))
        );

        assertThat(created.getStatus(), equalTo(Diff.Status.CREATED));
        assertThat(deleted.getStatus(), equalTo(Diff.Status.DELETED));
        assertThat(updated.getStatus(), equalTo(Diff.Status.UPDATED));
    }

    @Test
    public void shouldGetLatestElement() {
        final var thing = new Thing("A");
        final var children = Set.of(new Thing("B"));
        final var diff = new Diff(new Thing("C"), Set.of(new Thing("D")), thing, children);

        assertThat(diff.getElement(), is(thing));
        assertThat(diff.getDescendants(), is(children));
    }

    @Test
    public void shouldGetAfterIfBeforeIsNull() {
        final var thing = new Thing("A");
        final var children = Set.of(new Thing("B"));
        final var diff = new Diff(null, null, thing, children);

        assertThat(diff.getElement(), is(thing));
        assertThat(diff.getDescendants(), is(children));
    }

    @Test
    public void shouldGetBeforeIfAfterIsNull() {
        final var thing = new Thing("A");
        final var children = Set.of(new Thing("B"));
        final var diff = new Diff(thing, children, null, null);

        assertThat(diff.getElement(), is(thing));
        assertThat(diff.getDescendants(), is(children));
    }

    @Test
    public void shouldGetTddsTextWhenRelatedTddsAreSet() {
        final var thing = new Thing("A");
        final var children = Set.of(new Thing("B"));
        final var diff = new Diff(thing, children, null, null);

        HashMap<TddId, YamlTdd> relatedTo = new HashMap<>();
        relatedTo.put(new TddId("1"), YamlTdd.blank());
        diff.getElement().setRelatedTdds(relatedTo);

        assertTrue(diff.getElement().hasRelatedTdds());
        assertThat(diff.getElement().getRelatedTddsText(), hasItemInArray("1 - [SAMPLE TDD TEXT LONG TEXT FORMAT]\nLine 2\nLine 3"));
    }

    @EqualsAndHashCode(callSuper = false)
    @ToString
    private static class Thing extends DiffableWithRelatedTdds implements Diffable {
        @Getter
        private final String id;
        @Getter
        private final String name;
        @Getter
        private final C4Type type;
        @Getter
        @Setter
        private String[] relatedTo = new String[0];

        public Thing(String id) {
            this.id = id;
            name = "name";
            type = C4Type.PERSON;
        }
    }
}
