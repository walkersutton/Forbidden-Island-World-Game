
// Assignment 9.1
// Sutton, Walker
// walker
// Hong, Jiahao  
// hollis

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

//import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import tester.Tester;

// represents a single square of the game area
class Cell {

  // represents absolute height of this cell, in feet
  double height;

  // in logical coordinates, with the origin at the top-left corner of the
  // screen
  int x;
  int y;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;
  // reports whether this cell is flooded or not
  boolean isFlooded;

  Cell(double height, int x, int y) {
    this.height = height;
    this.x = x;
    this.y = y;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
    this.isFlooded = this.height < ForbiddenIslandWorld.waterHeight;

  }

  // creates the image of the cell with the correct color.
  public WorldImage cellImage() {
    Color col;
    int max = ForbiddenIslandWorld.ISLAND_SIZE / 2;
    double water = ForbiddenIslandWorld.waterHeight;
    this.isFlooded = (this.height < water) && (this.top.isFlooded || this.bottom.isFlooded
        || this.left.isFlooded || this.right.isFlooded);

    // draws a flooded cell
    if (this.isFlooded) {
      // col = Color.pink;
      col = new Color(0, 0, (int) (170 + (110 * (this.height - water) / max)));
    }

    // draws a non-flooded cell that is below the water height
    else if (!this.isFlooded) {
      col = new Color((int) (175 + 90 * -1 * (this.height - water) / max),
          (int) (130 - 110 * -((this.height - water) / max)), 0);
    }

    // draws a non-flooded cell that is above the water height
    else if ((this.height) <= ForbiddenIslandWorld.ISLAND_SIZE / 2) {
      col = new Color((int) (60 + (160 / max) * height), (int) (145 + (110 / max) * height),
          (int) (60 + (160 / max) * height));
    }
    // the peaks of very tall dry mountains have snow on top!
    else {
      col = Color.WHITE;
    }
    // creates the image of the Cell with the correct color
    return new RectangleImage(ForbiddenIslandWorld.SCALE, ForbiddenIslandWorld.SCALE, "solid", col);
  }
}

// to represent the OceanCell class
class OceanCell extends Cell {
  OceanCell(int x, int y) {
    super(-10.0, x, y);
  }

  // creates the image of the OceanCell with the correct color.
  public WorldImage cellImage() {
    Color col = new Color(0, 0, 170);
    return new RectangleImage(ForbiddenIslandWorld.SCALE, ForbiddenIslandWorld.SCALE, "solid", col);
  }
}

// to represent a GamePiece
// a GamePiece is either a player, a helicopter, or a part
class GamePiece {
  String name;
  int x = randomLandCell().x;
  int y = randomLandCell().y;

  // the helicopter is always placed on the highest point on the terrain
  // all other GamePieces are placed onto random land cells
  public GamePiece(String name) {
    if (name.equals("heli")) {
      this.x = peakCell().x;
      this.y = peakCell().y;
    }
    else {
      this.x = randomLandCell().x;
      this.y = randomLandCell().y;
    }
  }

  // returns a Posn of the highest Cell on the STARTING_BOARD
  Posn peakCell() {
    Posn position = new Posn(0, 0);
    for (int y = 0; y < ForbiddenIslandWorld.ISLAND_SIZE; y++) {
      for (int x = 0; x < ForbiddenIslandWorld.ISLAND_SIZE; x++) {
        if (ForbiddenIslandWorld.STARTING_BOARD.get(y).get(x) > ForbiddenIslandWorld.STARTING_BOARD
            .get(position.y).get(position.x)) {
          position = new Posn(x, y);
        }
      }
    }
    return position;
  }

  // returns a Posn of a random landCell on the starting board
  Posn randomLandCell() {
    Random rand = new Random();
    int x = rand.nextInt(rand.nextInt(ForbiddenIslandWorld.ISLAND_SIZE) + 1);
    int y = rand.nextInt(rand.nextInt(ForbiddenIslandWorld.ISLAND_SIZE) + 1);
    Posn position = new Posn(ForbiddenIslandWorld.ISLAND_SIZE / 2,
        ForbiddenIslandWorld.ISLAND_SIZE / 2);
    if (ForbiddenIslandWorld.STARTING_BOARD.get(y).get(x) > 5) {
      position = new Posn(x, y);
    }
    else {
      randomLandCell();
    }
    return position;
  }

  // determines if this GamePiece[player] is on top of another GamePiece
  // removes the part if the player is on top
  // this will only be ran on a player GamePiece
  void pickUp() {
    for (GamePiece g : ForbiddenIslandWorld.parts) {
      if ((this.x < g.x + ForbiddenIslandWorld.SCALE / 2)
          && (this.x > g.x - ForbiddenIslandWorld.SCALE / 2)
          && (this.y > g.y - ForbiddenIslandWorld.SCALE / 2)
          && (this.y < g.y + ForbiddenIslandWorld.SCALE / 2)) {
        /// REMOVEEEEEE
        g.x = ForbiddenIslandWorld.player.x;
        g.y = ForbiddenIslandWorld.player.y;
      }
    }
  }

  // determines if the player won the game
  // [if all of the parts have been picked and if the person is on the helicopter]
  public boolean endGame() {
    return ForbiddenIslandWorld.parts.isMt()
        && (this.x < ForbiddenIslandWorld.heli.x + ForbiddenIslandWorld.SCALE / 2)
        && (this.x > ForbiddenIslandWorld.heli.x - ForbiddenIslandWorld.SCALE / 2)
        && (this.y > ForbiddenIslandWorld.heli.y - ForbiddenIslandWorld.SCALE / 2)
        && (this.y < ForbiddenIslandWorld.heli.y + ForbiddenIslandWorld.SCALE / 2);
  }

  // creates the image of the GamePiece
  public WorldImage drawGamePiece(String type) {
    if (type.equals("player")) {
      WorldImage megaMan = new FromFileImage("megaman.png");
      ScaleImage scaledMan = new ScaleImage(megaMan, ForbiddenIslandWorld.SCALE * .0069);
      return scaledMan;
    }
    else if (type.equals("heli")) {
      WorldImage heli = new FromFileImage("helicopter.png");
      ScaleImage scaledHeli = new ScaleImage(heli, ForbiddenIslandWorld.SCALE * .0469);
      return scaledHeli;
    }
    else {
      WorldImage part = new FromFileImage("part.png");
      ScaleImage scaledPart = new ScaleImage(part, ForbiddenIslandWorld.SCALE * .0169);
      return scaledPart;
    }

  }
}

// to represent the ForbiddenIslandWorld class
class ForbiddenIslandWorld extends World {

  // the ISLAND_SIZE should be odd to create the perfect and random diamond
  // mountains
  // the ISLAND_SIZE for random terrain varies. Some trusted values are 65, 101,
  // and 201
  static final int ISLAND_SIZE = 65;

  // larger scale, bigger "bits/pixels"
  static final int SCALE = 8;

  // to initialize tick counter
  static int TICK_VAL = 0;

  // list of all the cells on the board, including the water
  IList<Cell> board;

  // list of all the parts on the board
  static IList<GamePiece> parts;

  // Random object used to create random values
  static Random rand;

  // numOfParts represents the number of parts on the board
  static int numOfParts;

  // ArrayList<ArrayList<Double>> of heights of dry Cells before the game begins
  static ArrayList<ArrayList<Double>> STARTING_BOARD;

  // the controlled player
  static GamePiece player;

  // the helicopter
  static GamePiece heli;

  // the current height of the water
  static int waterHeight;

  // *********************************************************************************************

  // plays the game
  ForbiddenIslandWorld() {
    this.board = new ArrayUtils().cellIList(neighborInit(doubleToCellAL(randomTerrainHeights())));
    ForbiddenIslandWorld.waterHeight = 0;
    ForbiddenIslandWorld.player = new GamePiece("player");
    ForbiddenIslandWorld.heli = new GamePiece("heli");
    ForbiddenIslandWorld.rand = new Random();
    ForbiddenIslandWorld.numOfParts = rand.nextInt(10) + 8;
    ForbiddenIslandWorld.parts = new MtList<GamePiece>();
    initParts();
  }

  // *********************************************************************************************

  // mutates the waterHeight of the board based on the number of ticks that pass
  // the waterHeight increases by one unit for every 10 ticks
  public void onTick() {

    if ((TICK_VAL - 10 == 0) && (waterHeight >= ISLAND_SIZE / 2 + 4)) {
      waterHeight = ISLAND_SIZE / 2 + 4;
      TICK_VAL = 10;
      ForbiddenIslandWorld.player.pickUp();
    }
    else if (TICK_VAL - 10 == 0) {
      waterHeight++;
      TICK_VAL = 0;
      ForbiddenIslandWorld.player.pickUp();
    }
    else {
      TICK_VAL++;
      ForbiddenIslandWorld.player.pickUp();
    }
  }

  /*- 
   * performs an action depending on what key was pressed
   * 
   * --esc Key: 
   * ends the current game
   * --r, m, t Key(s):
   * restarts the current game with
   * new GamePieces
   * m - new perfect mountain board
   * r - new random mountain board
   * t - new random terrain board
   * --up, right, down, left Key(s):
   * moves the player up, down, left, right by SCALE units
   * player cannot move off the board
   */
  public void onKeyEvent(String key) {
    if (key.equals("escape")) {
      this.endOfWorld("presumably a sad premature ending");
    }
    if (key.equals("r")) {
      resetWorld(1);
    }
    if (key.equals("m")) {
      resetWorld(2);
    }
    if (key.equals("t")) {
      resetWorld(3);
    }

    if (key.equals("left") && ForbiddenIslandWorld.player.x >= 1) {
      ForbiddenIslandWorld.player.x = ForbiddenIslandWorld.player.x - 1;
    }

    if (key.equals("right") && (ForbiddenIslandWorld.player.x <= (ISLAND_SIZE - SCALE / 4))) {
      ForbiddenIslandWorld.player.x = ForbiddenIslandWorld.player.x + 1;
    }

    if ((key.equals("up")) && (ForbiddenIslandWorld.player.y > 1)) {
      ForbiddenIslandWorld.player.y = ForbiddenIslandWorld.player.y - 1;
    }

    if ((key.equals("down")) && ForbiddenIslandWorld.player.y < (ISLAND_SIZE - SCALE / 4)) {
      ForbiddenIslandWorld.player.y = ForbiddenIslandWorld.player.y + 1;
    }

  }

  // produces the WorldScene of the game
  public WorldScene makeScene() {
    return drawer(this.board, ForbiddenIslandWorld.parts);
  }

  /*- 
   * restarts the current game with new GamePieces and a new board
   * 
   * input is 1, a random mountain board is created
   * input is 2, a perfect mountain board is created
   * input is 3, a random terrain board is created
   */
  public void resetWorld(int i) {
    // random mountain
    if (i == 1) {
      this.board = new ArrayUtils()
          .cellIList(neighborInit(doubleToCellAL(randomMountainHeights())));
      ForbiddenIslandWorld.waterHeight = 0;
      ForbiddenIslandWorld.player = new GamePiece("player");
      ForbiddenIslandWorld.heli = new GamePiece("heli");
      ForbiddenIslandWorld.rand = new Random();
      ForbiddenIslandWorld.numOfParts = rand.nextInt(10);
      ForbiddenIslandWorld.parts = new MtList<GamePiece>();
      initParts();
    }
    // perfect mountain
    if (i == 2) {
      this.board = new ArrayUtils().cellIList(neighborInit(doubleToCellAL(mountainHeights())));
      ForbiddenIslandWorld.waterHeight = 0;
      ForbiddenIslandWorld.player = new GamePiece("player");
      ForbiddenIslandWorld.heli = new GamePiece("heli");
      ForbiddenIslandWorld.rand = new Random();
      ForbiddenIslandWorld.numOfParts = rand.nextInt(10);
      ForbiddenIslandWorld.parts = new MtList<GamePiece>();
      initParts();
    }
    // random terrain
    if (i == 3) {
      this.board = new ArrayUtils().cellIList(neighborInit(doubleToCellAL(randomTerrainHeights())));
      ForbiddenIslandWorld.waterHeight = 0;
      ForbiddenIslandWorld.player = new GamePiece("player");
      ForbiddenIslandWorld.heli = new GamePiece("heli");
      ForbiddenIslandWorld.rand = new Random();
      ForbiddenIslandWorld.numOfParts = rand.nextInt(10);
      ForbiddenIslandWorld.parts = new MtList<GamePiece>();
      initParts();
    }
    // represents the end of the game
    if (player.endGame()) {
      this.board = new ArrayUtils().cellIList(neighborInit(doubleToCellAL(randomTerrainHeights())));
      ForbiddenIslandWorld.waterHeight = 0;
      ForbiddenIslandWorld.player = new GamePiece("player");
      ForbiddenIslandWorld.heli = new GamePiece("heli");
      ForbiddenIslandWorld.rand = new Random();
      ForbiddenIslandWorld.numOfParts = rand.nextInt(10);
      ForbiddenIslandWorld.parts = new MtList<GamePiece>();
      initParts();
    }
  }

  /*- 
   * game is stopped and the the final scene is displayed
   * is caused by pressing the esc key. A string appears saying the game has ended
   */
  public WorldScene lastScene(String s) {
    WorldScene scene = makeScene();
    scene.placeImageXY(new TextImage(s, SCALE * 2, Color.red),
        (ForbiddenIslandWorld.ISLAND_SIZE * 3), (ForbiddenIslandWorld.ISLAND_SIZE / 2));
    return scene;
  }

  // creates a WorldScene of all of the Cells and GamePieces
  // the player is on the top layer
  // the parts are below the player
  // the helicopter is below the parts
  // the Cells are below the helicopter [on the bottom layer]

  public WorldScene drawer(IList<Cell> list, IList<GamePiece> gList) {
    WorldScene scene = this.getEmptyScene();
    // draws all of the Cells
    for (Cell c : list) {
      scene.placeImageXY(c.cellImage(), (c.x) * SCALE + SCALE / 2, c.y * SCALE + SCALE / 2);
    }
    // draws the helicopter
    scene.placeImageXY(heli.drawGamePiece("heli"), heli.x * SCALE + SCALE / 2,
        heli.y * SCALE + SCALE / 2);
    // draws all of the parts
    for (GamePiece g : gList) {
      scene.placeImageXY(g.drawGamePiece("part"), g.x * SCALE + SCALE / 2, g.y * SCALE + SCALE / 2);
    }
    // draws the player
    scene.placeImageXY(player.drawGamePiece("player"), player.x * SCALE + SCALE / 2,
        player.y * SCALE + SCALE / 2);
    // returns the scene with all of the objects
    return scene;
  }

  // mutates the parts variable to a ConsList<GamePiece> which represents all of
  // the parts
  public void initParts() {
    for (int i = 0; i <= ForbiddenIslandWorld.numOfParts; i++) {
      ForbiddenIslandWorld.parts = new ConsList<GamePiece>(new GamePiece("part"),
          ForbiddenIslandWorld.parts);
    }
  }

  // creates an ArrayList<ArrayList<Cell>> which represents the Cells using
  // the given ArrayList<ArrayList<Double>> which represents the heights of
  // the Cells
  public ArrayList<ArrayList<Cell>> doubleToCellAL(ArrayList<ArrayList<Double>> list) {
    ArrayList<ArrayList<Cell>> result = new ArrayList<ArrayList<Cell>>();
    for (int y = 0; y < ForbiddenIslandWorld.ISLAND_SIZE; y++) {
      ArrayList<Cell> resultRow = new ArrayList<Cell>();
      for (int x = 0; x < ForbiddenIslandWorld.ISLAND_SIZE; x++) {
        if (getAL(list, x, y) < 0) {
          resultRow.add(new OceanCell(x, y));
        }
        else {
          resultRow.add(new Cell(list.get(y).get(x), x, y));
        }
      }
      result.add(resultRow);
    }
    return result;
  }

  // determine the Manhattan distance of the Cell at the given x y position
  public double manhattanHeight(int x, int y) {
    int halfSize = ForbiddenIslandWorld.ISLAND_SIZE / 2;
    return halfSize - (Math.abs(halfSize - x) + Math.abs(halfSize - y));
  }

  // creates an ArrayList<ArrayList<Double>> which represents the heights of the
  // Cells in a perfect diamond
  public ArrayList<ArrayList<Double>> mountainHeights() {
    ArrayList<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
    for (int y = 0; y < ForbiddenIslandWorld.ISLAND_SIZE; y++) {
      ArrayList<Double> resultRow = new ArrayList<Double>();
      for (int x = 0; x < ForbiddenIslandWorld.ISLAND_SIZE; x++) {
        resultRow.add(manhattanHeight(x, y));
      }
      result.add(resultRow);
    }
    // store the heights of the starting board in STARTING_BOARD
    STARTING_BOARD = result;
    return result;

  }

  // creates an ArrayList<ArrayList<Double>> which represents the heights of the
  // Cells in a random diamond
  public ArrayList<ArrayList<Double>> randomMountainHeights() {
    ArrayList<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
    Random rand = new Random();
    for (int y = 0; y < ForbiddenIslandWorld.ISLAND_SIZE; y++) {
      ArrayList<Double> resultRow = new ArrayList<Double>();
      for (int x = 0; x < ForbiddenIslandWorld.ISLAND_SIZE; x++) {
        if (manhattanHeight(x, y) > 0) {
          resultRow.add((double) Math.abs(rand.nextInt(ForbiddenIslandWorld.ISLAND_SIZE / 2) + 1));
        }
        else {
          resultRow.add(manhattanHeight(x, y));
        }
      }
      result.add(resultRow);
    }
    // store the heights of the starting board in STARTING_BOARD
    STARTING_BOARD = result;
    return result;
  }

  // creates an ArrayList<ArrayList<Double>> which represents the heights of the
  // Cells in a random terrain
  public ArrayList<ArrayList<Double>> randomTerrainHeights() {
    int size = ForbiddenIslandWorld.ISLAND_SIZE;
    int sizeM1 = ForbiddenIslandWorld.ISLAND_SIZE - 1;
    ArrayList<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
    for (int y = 0; y < size; y++) {
      ArrayList<Double> resultRow = new ArrayList<Double>();
      for (int x = 0; x < size; x++) {
        resultRow.add(0.0);
      }
      result.add(resultRow);
    }
    setAL(result, 0, sizeM1 / 2, 1.0); // TopMiddle
    setAL(result, sizeM1 / 2, 0, 1.0); // LeftMiddle
    setAL(result, sizeM1, sizeM1 / 2, 1.0); // BottomMiddle
    setAL(result, sizeM1 / 2, sizeM1, 1.0); // RightMiddle
    setAL(result, (int) Math.ceil(sizeM1 / 2), (int) Math.ceil(sizeM1 / 2),
        (double) Math.ceil(sizeM1 / 2)); // MiddleMiddle
    randomTerrainHeightsGenerator(result, 0, 0, sizeM1, sizeM1);

    // store the heights of the starting board in STARTING_BOARD
    STARTING_BOARD = result;
    return result;
  }

  /*-
   * mutates an ArrayList<ArrayList<Double>> of [0.0]'s to an
   * ArrayList<ArrayList<Double>> with "randomized" doubles
   * 
   * "randomized" is in quotation marks because the doubles aren't technically random. 
   * The values are determined using a midpoint formula and a true random shift 
   */
  public void randomTerrainHeightsGenerator(ArrayList<ArrayList<Double>> list, int tx, int ty,
      int bx, int by) {
    Random rand = new Random();
    Posn tl = new Posn(tx, ty);
    Posn br = new Posn(bx, by);
    int range = 4; // larger the more random
    double shift = 2.0; // modifies amount of water on average
    double nudge = .0051312857; // if too big, too similar. smaller, then more random

    // compute the double for the top midpoint
    double t = ((rand.nextInt(range) - shift) * nudge * (br.x - tl.x) * (br.y - tl.y))
        + ((getAL(list, tl.x, tl.y) + getAL(list, br.x, tl.y)) / 2);

    // compute the double for the bottom midpoint
    double b = ((rand.nextInt(range) - shift) * nudge * (br.x - tl.x) * (br.y - tl.y))
        + ((getAL(list, tl.x, br.y) + getAL(list, br.x, br.y)) / 2);

    // compute the double for the left midpoint
    double l = ((rand.nextInt(range) - shift) * nudge * (br.x - tl.x) * (br.y - tl.y))
        + ((getAL(list, tl.x, tl.y) + getAL(list, tl.x, br.y)) / 2);

    // compute the double for the right midpoint
    double r = ((rand.nextInt(range) - shift) * nudge * (br.x - tl.x) * (br.y - tl.y))
        + ((getAL(list, br.x, br.y) + getAL(list, br.x, tl.y)) / 2);

    // compute the double for the middle
    double m = ((rand.nextInt(range) - shift) * nudge * (br.x - tl.x) * (br.y - tl.y))
        + ((getAL(list, tl.x, tl.y) + getAL(list, br.x, tl.y) + getAL(list, br.x, br.y)
            + getAL(list, tl.x, br.y)) / 4);

    // check top
    if (getAL(list, ((br.x + tl.x) / 2), tl.y) == 0) {
      setAL(list, (br.x + tl.x) / 2, tl.y, t);
    }
    // check bottom
    if (getAL(list, ((br.x + tl.x) / 2), br.y) == 0) {
      setAL(list, (br.x + tl.x) / 2, br.y, b);
    }
    // check left
    if (getAL(list, tl.x, ((br.y + tl.y) / 2)) == 0) {
      setAL(list, tl.x, ((br.y + tl.y) / 2), l);
      System.out.println(l);
    }
    // check right
    if (getAL(list, br.x, ((br.y + tl.y) / 2)) == 0) {
      setAL(list, br.x, ((br.y + tl.y) / 2), r);
    }
    // check middle
    if (getAL(list, ((br.x + tl.x) / 2), (((br.y + tl.y) / 2))) == 0) {
      setAL(list, ((br.x + tl.x) / 2), ((br.y + tl.y) / 2), m);
    }
    // check final recursion
    if ((br.x - tl.x) < 3) {
      return;
    }

    // top right quadrant [Q1]
    randomTerrainHeightsGenerator(list, (br.x + tl.x) / 2, tl.y, br.x, (br.y + tl.y) / 2);

    // top left quadrant [Q2]
    randomTerrainHeightsGenerator(list, tl.x, tl.y, (br.x + tl.x) / 2, (br.y + tl.y) / 2);

    // bottom left quadrant [Q3]
    randomTerrainHeightsGenerator(list, tl.x, (br.y + tl.y) / 2, (br.x + tl.x) / 2, br.y);

    // bottom right quadrant [Q4]
    randomTerrainHeightsGenerator(list, (br.x + tl.x) / 2, (br.y + tl.y) / 2, br.x, br.y);
  }

  // mutates the ArrayList by overwriting the data at position x y to the input, T
  <T> void setAL(ArrayList<ArrayList<T>> list, int x, int y, T input) {
    list.get(y).set(x, input);
  }

  // returns the T at position x, y in the ArrayList
  <T> T getAL(ArrayList<ArrayList<T>> list, int x, int y) {
    return list.get(y).get(x);
  }

  // modifies each Cell in the given ArrayList<ArrayList<Cell>> to match up to
  // their appropriate neighbors
  ArrayList<ArrayList<Cell>> neighborInit(ArrayList<ArrayList<Cell>> list) {

    for (int y = 0; y < list.size(); y++) {
      for (int x = 0; x < list.get(y).size(); x++) {

        // top row Cells
        if (y == 0) {
          list.get(y).get(x).top = list.get(y).get(x);
          list.get(y).get(x).bottom = list.get(y + 1).get(x);
        }
        // bottom row Cells
        else if (y == list.size() - 1) {
          list.get(y).get(x).bottom = list.get(y).get(x);
          list.get(y).get(x).top = list.get(y - 1).get(x);
        }
        // in between top and bottom Cells
        else {
          list.get(y).get(x).bottom = list.get(y + 1).get(x);
          list.get(y).get(x).top = list.get(y - 1).get(x);
        }
        // left column Cells
        if (x == 0) {
          list.get(y).get(x).left = list.get(y).get(x);
          list.get(y).get(x).right = list.get(y).get(x + 1);
        }
        // right column Cells
        else if (x == list.size() - 1) {
          list.get(y).get(x).right = list.get(y).get(x);
          list.get(y).get(x).left = list.get(y).get(x - 1);
        }
        // in between left and right Cells
        else {
          list.get(y).get(x).right = list.get(y).get(x + 1);
          list.get(y).get(x).left = list.get(y).get(x - 1);
        }
      }
    }
    return list;
  }
}

// to represent the ArrayUtils class
class ArrayUtils {

  /*
   * takes an ArrayList<ArrayList<Cell>> and expands into an IList<Cell> in
   * standard English reading order (visually, the top left Cell in the
   * ArrayList<ArrayList<Cell>> will be the first Cell in the IList<Cell> and the
   * bottom right Cell in the ArrayList<ArrayList<Cell>> will be the last Cell in
   * the IList<Cell>
   */
  public <T> IList<T> cellIList(ArrayList<ArrayList<T>> list) {
    IList<T> currentList = new MtList<T>();
    for (int y = list.size() - 1; y > -1; y--) {
      for (int x = list.get(y).size() - 1; x > -1; x--) {
        currentList = new ConsList<T>(list.get(y).get(x), currentList);
      }
    }
    return currentList;
  }
}

// to represent the IList<T> interface
interface IList<T> extends Iterable<T> {

  // determines if this IList<T> is a ConsList<T>
  boolean isCons();

  // determines if this IList<T> is an MtList<T>
  boolean isMt();

  // returns what this IList<T> is Cons as
  ConsList<T> asCons();

}

// to represent the MtList<T> class
class MtList<T> implements IList<T> {

  // represents an Iterator for an MtList<T>
  public Iterator<T> iterator() {
    return new IListIterator<T>(this);
  }

  // determines if this MtList<T> is a ConsList<T>
  public boolean isCons() {
    return false;
  }

  // returns what the MtList<T> is Cons as
  public ConsList<T> asCons() {
    throw new IllegalArgumentException("An MtList<T> can never be a ConsList<T>");
  }

  // determines if this IList<T> is an MtList<T>
  public boolean isMt() {
    return true;
  }

}

// to represent the ConsList<T> class
class ConsList<Cell> implements IList<Cell> {
  Cell first;
  IList<Cell> rest;

  ConsList(Cell first, IList<Cell> rest) {
    this.first = first;
    this.rest = rest;
  }

  // represents an Iterator<T>
  public Iterator<Cell> iterator() {
    return new IListIterator<Cell>(this);
  }

  // determines if this Iterator<T> is Cons
  public boolean isCons() {
    return true;
  }

  // returns what this Iterator<T> is Cons as
  public ConsList<Cell> asCons() {
    return this;
  }

  // determines if this IList<T> is an MtList<T>
  public boolean isMt() {
    return false;
  }
}

// to represent the IListIterator<T> interface
class IListIterator<T> implements Iterator<T> {
  IList<T> items;

  IListIterator(IList<T> items) {
    this.items = items;
  }

  // determines if this IListIterator<T> is Cons
  public boolean hasNext() {
    return this.items.isCons();
  }

  // returns what this IListIterator<T> has next
  public T next() {
    ConsList<T> itemsAsCons = this.items.asCons();
    T answer = itemsAsCons.first;
    this.items = itemsAsCons.rest;
    return answer;
  }

  // silly method
  public void remove() {
    throw new UnsupportedOperationException(
        "Silly you! You should know to never use remove. Keep it all! Be inclusive!");
  }
}

// to represent the examples class
class ExampleFlood {

  // main method to run the game
  public static void main(String[] argv) {
    ForbiddenIslandWorld w = new ForbiddenIslandWorld();
    w.bigBang(ForbiddenIslandWorld.ISLAND_SIZE * ForbiddenIslandWorld.SCALE,
        ForbiddenIslandWorld.ISLAND_SIZE * ForbiddenIslandWorld.SCALE, .1);
  }

  // examples of Cells

  Cell c1 = new Cell(2, 2, 2);
  Cell c2 = new Cell(0, 2, 2);
  Cell c3 = new Cell(-5, 2, 2);

  // examples of OceanCells

  OceanCell oc1 = new OceanCell(3, 4);

  // examples of ArrayUtils

  ArrayUtils a1 = new ArrayUtils();

  // examples of ForbiddenIslandWorlds

  ForbiddenIslandWorld f1 = new ForbiddenIslandWorld();

  // examples of ArrayLists
  // maybe want to break into AL<Cell>, AL<AL<Cell>>, AL<double>, AL<AL<double>>,
  // etc.

  ArrayList<Cell> alc1 = new ArrayList<Cell>(Arrays.asList(c1, c2, c3));
  ArrayList<Cell> alc2 = new ArrayList<Cell>(Arrays.asList(c1, c1, c3));
  ArrayList<Cell> alc3 = new ArrayList<Cell>(Arrays.asList(c3, c2, c3));
  ArrayList<ArrayList<Cell>> alal1 = new ArrayList<ArrayList<Cell>>(
      Arrays.asList(alc1, alc2, alc3));
  ArrayList<Double> ald1 = new ArrayList<Double>(Arrays.asList(1.0, 2.0, 3.0));
  ArrayList<Double> ald2 = new ArrayList<Double>(Arrays.asList(1.0, 1.0, 3.0));
  ArrayList<Double> ald3 = new ArrayList<Double>(Arrays.asList(3.0, 2.0, 3.0));
  ArrayList<ArrayList<Double>> alald1 = new ArrayList<ArrayList<Double>>(
      Arrays.asList(ald1, ald2, ald3));
  MtList<Cell> mtc1 = new MtList<Cell>();

  // examples of Lists<Cell>

  ConsList<Cell> clc1 = new ConsList<Cell>(c1,
      new ConsList<Cell>(c2,
          new ConsList<Cell>(c3, new ConsList<Cell>(c1, new ConsList<Cell>(c1, new ConsList<Cell>(
              c3,
              new ConsList<Cell>(c3, new ConsList<Cell>(c2, new ConsList<Cell>(c3, mtc1)))))))));

  // to test cellImage
  boolean testCellImage(Tester t) {
    return t.checkExpect(c1.cellImage(),
        new RectangleImage(10, 10, OutlineMode.SOLID, new Color(0, 150, 0, 241)))
        && t.checkExpect(c2.cellImage(),
            new RectangleImage(10, 10, OutlineMode.SOLID, new Color(0, 0, 255)))
        && t.checkExpect(c3.cellImage(),
            new RectangleImage(10, 10, OutlineMode.SOLID, new Color(0, 0, 255)));
  }

  // to test peakCell
  boolean testPeakCell(Tester t) {
    return t.checkExpect(a1.cellIList(alal1), clc1);
  }

  // to test randomLandCell
  boolean testRandomLandCell(Tester t) {
    return t.checkExpect(a1.cellIList(alal1), clc1);
  }

  // to test pickUp
  boolean testPickUp(Tester t) {
    return t.checkExpect(a1.cellIList(alal1), clc1);
  }

  // to test endGame
  boolean testEndGame(Tester t) {
    return t.checkExpect(a1.cellIList(alal1), clc1);
  }

  // to test drawGamePiece
  boolean testDrawGamePiece(Tester t) {
    return t.checkExpect(a1.cellIList(alal1), clc1);
  }

  // to test onTick
  boolean testOnTick(Tester t) {
    return t.checkExpect(a1.cellIList(alal1), clc1);
  }

  // to test onKeyEvent
  boolean testOnKeyEvent(Tester t) {
    return t.checkExpect(a1.cellIList(alal1), clc1);
  }

  // to test makeScene
  boolean testMakeScene(Tester t) {
    return t.checkExpect(a1.cellIList(alal1), clc1);
  }

  // to test resetWorld
  boolean testResetWorld(Tester t) {
    return t.checkExpect(a1.cellIList(alal1), clc1);
  }

  // to test lastScene
  boolean lastScene(Tester t) {
    return t.checkExpect(a1.cellIList(alal1), clc1);
  }

  // to test drawer
  boolean testDrawere(Tester t) {
    return t.checkExpect(a1.cellIList(alal1), clc1);
  }

  // to test initParts
  boolean testInitParts(Tester t) {
    return t.checkExpect(a1.cellIList(alal1), clc1);
  }

  // to test doubleToCellAL
  boolean testDoubleToCellAL(Tester t) {
    return t.checkExpect(a1.cellIList(alal1), clc1);
  }

  // to test manhattanHeight
  boolean testManhattanHeight(Tester t) {
    return t.checkExpect(f1.manhattanHeight(2, 2), -28.0)
        && t.checkExpect(f1.manhattanHeight(32, 32), 32.0)
        && t.checkExpect(f1.manhattanHeight(0, 5), -27.0)
        && t.checkExpect(f1.manhattanHeight(10, 0), -22.0);
  }

  // to test cellIList
  boolean testCellIList(Tester t) {
    return t.checkExpect(a1.cellIList(alal1), clc1);
  }

  // to test heightsFuncMountain
  boolean testHeightsFuncMountain(Tester t) {
    return t.checkExpect(f1.mountainHeights(), alald1);
  }

  // to test heightsFuncRandom
  boolean testHeightsRandom(Tester t) {
    return t.checkExpect(f1.randomMountainHeights(), alald1);
  }

  // to test cellsFunc
  boolean testCellsFunc(Tester t) {
    return t.checkExpect(f1.doubleToCellAL(alald1), alal1);
  }

  // to test randomTerrainsFund
  boolean testRandomTerrainsFunc(Tester t) {
    return t.checkExpect(a1.cellIList(alal1), clc1);
  }

}
