package demo.swfactory.model

class Fragment {
    Build build
    Build.State state
    Result result
    Set<String> resultKeys

    //updated collection for updatedFragment
    Set<String> updated

    String key() {
        if (build) {
            return build.key()
        } else {
            return result.key()
        }
    }

    String trackingId() {
        if (build) {
            return build.trackingId
        } else {
            return result.trackingId
        }
    }

    @Override
    String toString() {
        return "Fragment{" +
                "build=" + build +
                ", result=" + result +
                ", resultKeys=" + resultKeys +
                ", updated=" + updated +
                '}';
    }
}
