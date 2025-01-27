package hProjekt.controller.actions;

import hProjekt.controller.PlayerController;

/**
 * An action to roll the dice.
 */
public class RollDiceAction implements PlayerAction {

    @Override
    public void execute(PlayerController pc) throws IllegalActionException {
        pc.rollDice();
    }
}
