package net.trilogy.arch.domain.architectureUpdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.trilogy.arch.domain.architectureUpdate.YamlFunctionalArea.FunctionalAreaId;
import net.trilogy.arch.domain.architectureUpdate.YamlFunctionalRequirement.FunctionalRequirementId;
import net.trilogy.arch.domain.architectureUpdate.YamlDecision.DecisionId;

import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.PROPERTIES;
import static java.util.stream.Collectors.toList;

@Getter
@ToString
@EqualsAndHashCode
@JsonPropertyOrder(value = {
        "name",
        "milestone",
        "authors",
        "PCAs",
        "P2",
        "P1",
        "useful-links",
        "milestone-dependencies",
        "decisions",
        "tdds-per-component",
        "functional-requirements",
        "capabilities"
})
public class YamlArchitectureUpdate {
    public static final String ARCHITECTURE_UPDATE_YML = "architecture-update.yml";

    @JsonProperty(value = "name")
    private final String name;
    @JsonProperty(value = "milestone")
    private final String milestone;
    @JsonProperty(value = "authors")
    private final List<YamlPerson> authors;
    @JsonProperty(value = "PCAs")
    private final List<YamlPerson> PCAs;
    @JsonProperty(value = "P2")
    private final YamlP2 p2;
    @JsonProperty(value = "P1")
    private final YamlP1 p1;
    @JsonProperty(value = "useful-links")
    private final List<YamlLink> usefulLinks;
    @JsonProperty(value = "milestone-dependencies")
    private final List<YamlMilestoneDependency> milestoneDependencies;
    @JsonProperty(value = "decisions")
    private final Map<DecisionId, YamlDecision> decisions;
    @JsonProperty(value = "tdds-per-component")
    private final List<YamlTddContainerByComponent> tddContainersByComponent;
    @JsonProperty(value = "functional-requirements")
    private final Map<FunctionalRequirementId, YamlFunctionalRequirement> functionalRequirements;
    @JsonProperty(value = "functional-areas")
    private final Map<FunctionalAreaId, YamlFunctionalArea> functionalAreas;
    @JsonProperty(value = "capabilities")
    private final YamlCapabilitiesContainer capabilityContainer;

    @Builder(toBuilder = true)
    @JsonCreator(mode = PROPERTIES)
    public YamlArchitectureUpdate(
            @JsonProperty("name") String name,
            @JsonProperty("milestone") String milestone,
            // TODO: Smell: Author and PCA persons are identical types
            @JsonProperty("authors") List<YamlPerson> authors,
            @JsonProperty("PCAs") List<YamlPerson> PCAs,
            // TODO: Smell: Do decisions not know their ID?
            @JsonProperty("decisions") Map<DecisionId, YamlDecision> decisions,
            // TODO: Smell: The subtype is overly complex -- could the real type be passed in?
            @JsonProperty("tdds-per-component") List<YamlTddContainerByComponent> tddContainersByComponent,
            // TODO: Smell: Do func reqs not know their own ID?
            @JsonProperty("functional-requirements") Map<FunctionalRequirementId, YamlFunctionalRequirement> functionalRequirements,
            @JsonProperty("functional-areas") Map<FunctionalAreaId, YamlFunctionalArea> functionalAreas,
            @JsonProperty("capabilities") YamlCapabilitiesContainer capabilityContainer,
            @JsonProperty("p2") YamlP2 p2,
            @JsonProperty("p1") YamlP1 p1,
            @JsonProperty("useful-links") List<YamlLink> usefulLinks,
            @JsonProperty("milestone-dependencies") List<YamlMilestoneDependency> milestoneDependencies) {
        this.name = name;
        this.milestone = milestone;
        this.authors = authors;
        this.PCAs = PCAs;
        this.decisions = decisions;
        this.tddContainersByComponent = tddContainersByComponent;
        this.functionalRequirements = functionalRequirements;
        this.functionalAreas = functionalAreas;
        this.capabilityContainer = capabilityContainer;
        this.p2 = p2;
        this.p1 = p1;
        this.usefulLinks = usefulLinks;
        this.milestoneDependencies = milestoneDependencies;
    }

    /** @todo How does this differ from {@link #blank()}? */
    public static YamlArchitectureUpdateBuilder prefilledYamlArchitectureUpdateWithBlanks() {
        return YamlArchitectureUpdate.builder()
                .name("[SAMPLE NAME]")
                .milestone("[SAMPLE MILESTONE]")
                .authors(List.of(YamlPerson.blank()))
                .PCAs(List.of(YamlPerson.blank()))
                .decisions(Map.of(DecisionId.blank(), YamlDecision.blank()))
                .tddContainersByComponent(List.of(YamlTddContainerByComponent.blank()))
                .functionalRequirements(Map.of(FunctionalRequirementId.blank(), YamlFunctionalRequirement.blank()))
                .functionalAreas(Map.of(FunctionalAreaId.blank(), YamlFunctionalArea.blank()))
                .capabilityContainer(YamlCapabilitiesContainer.blank())
                .p2(YamlP2.blank())
                .p1(YamlP1.blank())
                .usefulLinks(List.of(YamlLink.blank()))
                .milestoneDependencies(List.of(YamlMilestoneDependency.blank()));
    }

    public static YamlArchitectureUpdate blank() {
        return prefilledYamlArchitectureUpdateWithBlanks().build();
    }

    public YamlArchitectureUpdate addJiraToFeatureStory(YamlFeatureStory storyToChange, YamlJira jiraToAdd) {
        return toBuilder().capabilityContainer(
                getCapabilityContainer().toBuilder()
                        .featureStories(
                                getCapabilityContainer().getFeatureStories().stream()
                                        .map(story -> {
                                            if (story.equals(storyToChange)) {
                                                return story.toBuilder().jira(jiraToAdd).build();
                                            }
                                            return story;
                                        })
                                        .collect(toList()))
                        .build())
                .build();
    }
}
