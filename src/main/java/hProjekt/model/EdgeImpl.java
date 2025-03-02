package hProjekt.model;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.tudalgo.algoutils.student.annotation.StudentImplementationRequired;

import hProjekt.Config;
import javafx.beans.property.Property;
import javafx.util.Pair;

/**
 * Default implementation of {@link Edge}.
 *
 * @param grid       the HexGrid instance this edge is placed in
 * @param position1  the first position
 * @param position2  the second position
 * @param railOwners the road's owner, if a road has been built on this edge
 */
public record EdgeImpl(HexGrid grid, TilePosition position1, TilePosition position2, Property<List<Player>> railOwners)
        implements Edge {
    @Override
    public HexGrid getHexGrid() {
        return grid;
    }

    @Override
    public TilePosition getPosition1() {
        return position1;
    }

    @Override
    public TilePosition getPosition2() {
        return position2;
    }

    @Override
    public Property<List<Player>> getRailOwnersProperty() {
        return railOwners;
    }

    @Override
    @StudentImplementationRequired("P1.3")
    public Set<Edge> getConnectedRails(final Player player) {
        // TODO: P1.3
        //Jiawen write on 25-02-2025
        //this edge located on HexGrid "grid", look up all rails of player by calling grid.getRails()
        //filter the connected ones with connectsTo
        return grid.getRails(player).values().stream().filter(this::connectsTo).collect(Collectors.toSet());
        //return org.tudalgo.algoutils.student.Student.crash("P1.3 - Remove if implemented");
    }

    @Override
    public Map<Player, Integer> getRentingCost(Player player) {
        if (getRailOwners().contains(player)) {
            return Map.of();
        }
        return getRailOwners().stream().collect(Collectors.toMap(p -> p, p -> 1));
    }

    @Override
    public int getDrivingCost(TilePosition from) {
        if (!getAdjacentTilePositions().contains(from)) {
            throw new IllegalArgumentException("The given position is not adjacent to this edge.");
        }
        return Config.TILE_TYPE_TO_DRIVING_COST.get(new Pair<Tile.Type, Tile.Type>(
                getHexGrid().getTileAt(from).getType(),
                getHexGrid().getTileAt(getPosition1().equals(from) ? getPosition2() : getPosition1()).getType()));
    }

    @Override
    public int getTotalBuildingCost(Player player) {
        return getBaseBuildingCost() + getTotalParallelCost(player);
    }

    @Override
    public int getTotalParallelCost(Player player) {
        return getParallelCostPerPlayer(player).values().stream().reduce(0, Integer::sum);
    }

    @Override
    public Map<Player, Integer> getParallelCostPerPlayer(Player player) {
        final Map<Player, Integer> result = new HashMap<>();
        if ((!getRailOwners().isEmpty()) && (!((getRailOwners().size() == 1) && getRailOwners().contains(player)))) {
            if (Collections.disjoint(getHexGrid().getCities().keySet(), getAdjacentTilePositions())) {
                getRailOwners().stream().forEach(p -> result.put(p, 5));
            } else {
                getRailOwners().stream().forEach(p -> result.put(p, 3));
            }
        }
        getAdjacentTilePositions().stream().flatMap(position -> {
            if (getHexGrid().getCityAt(position) != null) {
                return Stream.empty();
            }
            Set<Player> owners = getHexGrid().getTileAt(position).getEdges().stream()
                    .filter(Predicate.not(this::equals)).flatMap(edge -> edge.getRailOwners().stream())
                    .collect(Collectors.toUnmodifiableSet());
            if (owners.contains(player)) {
                return Stream.empty();
            }
            return owners.stream();
        }).forEach(p -> result.put(p, Math.max(result.getOrDefault(p, 0), 1)));
        return result;
    }

    @Override
    public int getBaseBuildingCost() {
        return Config.TILE_TYPE_TO_BUILDING_COST.get(getAdjacentTilePositions().stream()
                .map(position -> getHexGrid().getTileAt(position).getType()).collect(Collectors.toUnmodifiableSet()));
    }

    @Override
    public List<Player> getRailOwners() {
        return getRailOwnersProperty().getValue();
    }

    @Override
    public boolean removeRail(Player player) {
        return getRailOwnersProperty().getValue().remove(player);
    }

    @Override
    @StudentImplementationRequired("P1.3")
    public boolean addRail(Player player) {
        // TODO: P1.3
        //Jiawen alters on 02-03-2025
        //if the player has already built a rail here...
        if(this.getRailOwners().contains(player)) {
            return false;
        }
        //if the player has not built any rails
        if(player.getRails().isEmpty()){
            City cityAt1 = getHexGrid().getCityAt(position1);
            City cityAt2 = getHexGrid().getCityAt(position2);
            //there is no city on position 1
            if(cityAt1 == null){
                //there is no city on position 2
                if(cityAt2 == null){
                    return false;
                } else if (!cityAt2.isStartingCity()) {
                    //there is a city on position 2, but it is not the staring city
                    return false;
                }
            } else {
                //there is a city on position 1, but it is not the staring city
                if(!cityAt1.isStartingCity()){
                    //there is no city on position 2
                    if(cityAt2 == null){
                        return false;
                    } else if (!cityAt2.isStartingCity()) {
                        //there is a city on position 2, but it is not the staring city
                        return false;
                    }
                }
            }
        }
        //if the player has no connected rails
        if(getConnectedRails(player).isEmpty()){
            return false;
        }
        //Set player list
        getRailOwners().add(player);

        //it doesn't work as below, since add() returns boolean!
        //railOwners.setValue(addPlayer.add(player));

        return true;
        //return org.tudalgo.algoutils.student.Student.crash("P1.3 - Remove if implemented");
    }

    @Override
    public boolean hasRail() {
        return (getRailOwnersProperty().getValue() != null) && (!getRailOwnersProperty().getValue().isEmpty());
    }

    @Override
    @StudentImplementationRequired("P1.3")
    public boolean connectsTo(Edge other) {
        // TODO: P1.3
        //Jiawen alters on 02-03-2025
        //only one of this two positions should equal to one of the position of "other"
        if (position1.equals(other.getPosition1()) && !position2.equals(other.getPosition2())) {
            return true;
        } else if (position1.equals(other.getPosition2()) && !position2.equals(other.getPosition1())) {
            return true;
        } else if (position2.equals(other.getPosition1()) && !position1.equals(other.getPosition2())) {
            return true;
        } else if (position2.equals(other.getPosition2()) && !position1.equals(other.getPosition1())) {
            return true;
        }
        return false;
        //return org.tudalgo.algoutils.student.Student.crash("P1.3 - Remove if implemented");
    }

    @Override
    public Set<TilePosition> getAdjacentTilePositions() {
        return Set.of(getPosition1(), getPosition2());
    }

    @Override
    @StudentImplementationRequired("P1.3")
    public Set<Edge> getConnectedEdges() {
        // TODO: P1.3
        //Jiawen write on 25-02-2025
        //this edge located on HexGrid "grid", look up all edges by calling grid.getEdges()
        //filter the edges with connectsTo, collect them in a Set
        return grid.getEdges().values().stream().filter(this::connectsTo).collect(Collectors.toSet());
        //return org.tudalgo.algoutils.student.Student.crash("P1.3 - Remove if implemented");
    }
}
