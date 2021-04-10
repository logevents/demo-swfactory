package demo.swfactory.model

class Fragment {
    Build build
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
