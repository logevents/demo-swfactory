package demo.swfactory.pusher.generation

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.ThreadLocalRandom

class RandomComponentNames {
    private static Logger LOG = LoggerFactory.getLogger(RandomComponentNames.class)

    static final Set<String> words = [
            "Factory", "Bean", "Wrapper", "Visitor", "Model", "Singleton",
            "Method", "Configuration", "Exception", "Error", "Property", "Value",
            "Identifier", "Attribute", "Authentication", "Policy", "Container",
            "Order", "Info", "Parameter", "Request", "Adapter", "Bridge",
            "Decorator", "Facade", "Proxy", "Worker",
            "Interpreter", "Iterator", "Observer",
            "State", "Strategy", "Template", "Comparator", "Clone", "Task",
            "Resolver", "Candidate", "Expression", "Predicate",
            "Thread", "Pool", "Descriptor", "Interceptor", "Definition",
            "Getter", "Setter", "Listener", "Proccesor", "Printer",
            "Prototype", "Composer", "Event", "Helper", "Utils",
            "Invocation", "Exporter", "Importer", "Serializer", "Callback",
            "Tag", "Context", "Mapping", "Advisor", "Filter", "Field", "Test",
            "Tests", "Connection", "Annotation", "Service", "Repository",
            "Stub", "Mock", "Instance", "Dispatcher", "Client", "Server",
            "Message", "Map", "List", "Collection", "Queue", "Manager",
            "Database", "Reponse", "Broadcaster",
            "Watcher", "Schema", "Mapper", "Publisher", "Consumer", "Producer"].toSet()

    static final Set<String> inWords = [
            "Composite", "Invalid", "Supported", "Focus", "Traversal", "Abstract",
            "Transformer", "Common", "Concrete", "Autowire", "Simple", "Aware",
            "Aspect", "Principal", "Driven", "Interruptible", "Batch",
            "Prepared", "Statement", "Remote", "Stateless", "Session",
            "Transaction", "Transactional", "Based", "Meta", "Data", "Jms",
            "Readable", "Literal", "Reflective", "Scope", "Multipart", "Xml",
            "Generic", "Interface", "Advisable", "Observable", "Identifiable",
            "Iterable", "Distributed", "Notification", "Failure", "Type",
            "Http", "Jdbc"].toSet()

    static String generate(max) {
        List<String> pool = new ArrayList<>()
        pool.addAll(words)
        pool.addAll(inWords)
        def word = ""
        def curWord
        for (int i = 0; i < max; i++) {
            while (true) {
                curWord = pool.get((int) (Math.random() * (pool.size() - 1)))
                if (word.indexOf(curWord) == -1) {
                    break;
                }
            }
            word += curWord;
        }
        LOG.info("Generated: {}", word)
        return word;
    }

}
