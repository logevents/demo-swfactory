package demo.swfactory.pusher


import demo.swfactory.model.CollectedBuild
import demo.swfactory.pusher.generation.CollectedBuildGeneration

class Pusher {
    private String target

    static void main(String[] args) {

        new Pusher("localhost:9092").push()
    }

    Pusher(String target) {
        this.target = target
    }

    void push() {

        CollectedBuild collectedBuild = CollectedBuildGeneration.generateEasyBuild()

        System.out.println("Created "+collectedBuild.toString())
    }
}
