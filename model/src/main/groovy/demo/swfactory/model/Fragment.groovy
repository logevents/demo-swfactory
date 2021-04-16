package demo.swfactory.model

class Fragment {
    Build build
    Build.State state
    Result result
    Set<String> resultKeys

    String key() {
        if (build) {
            return build.key()
        } else {
            return result.key()
        }
    }
}
