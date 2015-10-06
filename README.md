# Elevator Control System


### Interfaces

There are two main interfaces. The ElevatorControlSystem

```Scala
trait ElevatorControlSystem {
  def status: Seq[ElevatorStatus]
  def update(status: ElevatorStatus): Unit
  def requestPickup(floor: Int, direction: Direction): Unit
  def step: Unit
}
```

and the ElevatorControl

```Scala
trait ElevatorControl {
  def move(direction: Direction): Unit
  def openDoor(direction: Direction): Unit
  def status: ElevatorStatus
}
```

where the ElevatorStatus is

```Scala
case class ElevatorStatus(floor: Int,
                          goals: List[Int],
                          direction: Option[Direction],
                          isLoading: Boolean)
```


### Data Structures

The `ElevatorControlSystem` needs to have a representation of the 
`PickupRequest`'s that have been made. I use Scala's mutable Queue to store 
the pickup requests.

For the simulation, I represent an Elevator with a floor number and
passengers. I use a Set to represent the set of passengers on the elevator.

### Algorithm

On each step, each elevator can move up, move down, open the door, or 
do nothing.

For an elevator that has passengers

* if the current floor is a goal floor, open the door
* if the current floor has a pickup request in the same direction, open the door
* otherwise, continue to the goals

For an elevator that has no passengers

* If there are any pickup requests, move to one of them and then open the door
* otherwise, do nothing.

The scheduling algorithm depends on the choice of which pickup request will be 
chosen by an empty elevator. My algorithm chooses either the lowest Up request, 
or the highest Down request. This is meant to make the scheduling more fair 
for passengers at the top and bottom of a building.


### Instructions

To run the simulation

```
sbt run
```
