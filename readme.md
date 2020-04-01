## Expression checker sample in kafka streams

An application to allow users create a dynamic expression to filter out contents from 
a JSON stream. 

Many times applications need a dynamic way to write filters for a JSON stream.

This app allows you to define an environment variable to create an expression which will be applied to an incoming stream.

The expression filter is created using a syntax which is explained as follows.

The source json data is loaded into a variable called `$`.

Say your source json looks like this 
`
    {
    	"city": "dallas",
    	"state": "TX",
    	"name": "rahul"
    }
`

Now you want a filter which will filter out `name == rahul`.

Since your parsed json(JsonNode) is loaded into a variable `$`, your expression will be 
`$.get("name").asText().equals("rahul")`

You can read the [JsonNode](https://fasterxml.github.io/jackson-databind/javadoc/2.7/com/fasterxml/jackson/databind/JsonNode.html) API documentation on how to use this object


The demo uses [Confluent cloud](https://confluent.cloud/)

| Environment Variable  | Description |
| --- |  --- |
| BOOTSTRAP_SERVERS  | URL of the bootstrap servers |
| API_KEY |  API key of CCloud |
| SECRET |  Secret of CCloud |
| topic | topic name for the source topic |
| expression | Expression to evaluate |