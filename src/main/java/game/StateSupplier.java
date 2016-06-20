package game;

import game.Player.State;

interface StateSupplier {

    State first();

    State next(State previous);
}
