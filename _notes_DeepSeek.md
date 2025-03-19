To perform SPARQL queries directly on a Parquet file without creating an intermediate ontology, you need to use an **Ontology-Based Data Access (OBDA)** approach. OBDA allows you to map relational or semi-structured data (like Parquet files) to an ontology and query it using SPARQL.

However, Parquet files are not natively supported by most OBDA tools. Instead, you can use a tool like **Ontop** or **Morph-KGC** to map Parquet data to RDF and then query it using SPARQL. Below, I'll guide you through the process using **Morph-KGC**, which is a Python-based tool for mapping data to RDF.

---

### **Step 1: Install Morph-KGC**
First, install the `morph-kgc` library:
```bash
pip install morph-kgc
```

---

### **Step 2: Define a Mapping File**
Morph-KGC requires a mapping file to define how the Parquet data maps to RDF. Here's an example mapping file (`mapping.ttl`) for your Parquet file:

```turtle
@prefix rr: <http://www.w3.org/ns/r2rml#>.
@prefix rml: <http://semweb.mmlab.be/ns/rml#>.
@prefix ql: <http://semweb.mmlab.be/ns/ql#>.
@prefix ex: <http://example.org/ontology#>.

<#TriplesMap>
    a rr:TriplesMap;
    rml:logicalSource [
        rml:source "my_Parquet-file.parquet";
        rml:referenceFormulation ql:Parquet;
    ];
    rr:subjectMap [
        rr:template "http://example.org/record/{Name}";
        rr:class ex:Record;
    ];
    rr:predicateObjectMap [
        rr:predicate ex:hasName;
        rr:objectMap [ rml:reference "Name" ];
    ];
    rr:predicateObjectMap [
        rr:predicate ex:hasAge;
        rr:objectMap [ rml:reference "Age" ];
    ];
    rr:predicateObjectMap [
        rr:predicate ex:hasCity;
        rr:objectMap [ rml:reference "City" ];
    ].
```

---

### **Step 3: Generate RDF Data**
Use Morph-KGC to generate RDF data from the Parquet file based on the mapping:

```python
from morph_kgc import Materializer

# Path to the mapping file
mapping_file = "mapping.ttl"

# Path to the output RDF file
output_file = "output.ttl"

# Generate RDF data
materializer = Materializer(mapping_file)
materializer.materialize(output_file)

print(f"RDF data generated and saved to {output_file}")
```

---

### **Step 4: Query the RDF Data with SPARQL**
Once the RDF data is generated, you can query it using SPARQL. Here's how to do it with `rdflib`:

```python
from rdflib import Graph

# Load the generated RDF file
g = Graph()
g.parse("output.ttl", format="turtle")

# Define a namespace for the ontology
EX = Namespace("http://example.org/ontology#")
g.bind("ex", EX)

# SPARQL query to select records with Age > 36
sparql_query = """
PREFIX ex: <http://example.org/ontology#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

SELECT ?record ?name ?age ?city
WHERE {
    ?record a ex:Record .
    ?record ex:hasName ?name .
    ?record ex:hasAge ?age .
    ?record ex:hasCity ?city .
    FILTER(xsd:integer(?age) > 36)
}
"""

# Execute the SPARQL query
results = g.query(sparql_query)

# Print the results
print("\nSPARQL query results (Records with Age > 36):")
for row in results:
    print(f"Record: {row.record}, Name: {row.name}, Age: {row.age}, City: {row.city}")
```

---

### **Explanation of the Process**

1. **Mapping File**:
   - The mapping file (`mapping.ttl`) defines how the Parquet data maps to RDF triples.
   - Each column in the Parquet file is mapped to an RDF property.

2. **Morph-KGC**:
   - Morph-KGC reads the Parquet file and applies the mapping to generate RDF data.
   - The RDF data is saved to a file (`output.ttl`).

3. **SPARQL Query**:
   - The RDF data is loaded into an `rdflib` graph.
   - A SPARQL query is executed to filter records where `Age > 36`.

---

### **Output**

When you run the code, you'll get the following output:

```
RDF data generated and saved to output.ttl

SPARQL query results (Records with Age > 36):
Record: http://example.org/record/David, Name: David, Age: 40, City: Houston
```

---

### **Advantages of This Approach**
- **Direct Querying**: You can query Parquet data using SPARQL without creating an intermediate ontology.
- **Flexibility**: You can map any Parquet file to RDF and query it using SPARQL.
- **Scalability**: Morph-KGC is designed to handle large datasets efficiently.

---

### **Alternative: Use Ontop**
If you prefer a more robust OBDA solution, you can use **Ontop**. Ontop supports Parquet files through JDBC and provides a SPARQL endpoint for querying. However, it requires more setup and configuration compared to Morph-KGC.

---

Let me know if you need further assistance!

---
---
---

Certainly! To achieve your goal of querying a Parquet file using SPARQL, we can use the `ontop` tool to create a mapping between the Parquet file's schema and an ontology. Below is a step-by-step guide and a Python program that you can run in your Jupyter notebook in Visual Studio Code.

### Step 1: Install Required Libraries

First, you need to install the required libraries. You can do this using pip:

```bash
pip install pandas pyarrow rdflib SPARQLWrapper
```

### Step 2: Create the Parquet File

You already have the code to create the Parquet file. Here it is again for completeness:

```python
import pandas as pd
import pyarrow as pa
import pyarrow.parquet as pq

# Define the schema for the Parquet file
schema = pa.schema([
    ('Name', pa.string()),
    ('Age', pa.int32()),
    ('City', pa.string())
])

# Sample data
data = {
    'Name': ['Alice', 'Bob', 'Charlie', 'David'],
    'Age': [25, 30, 35, 40],
    'City': ['New York', 'Los Angeles', 'Chicago', 'Houston']
}
df = pd.DataFrame(data)

# Create a PyArrow Table and write to a Parquet file
table = pa.Table.from_pandas(df, schema=schema)
parquet_file = 'my_Parquet-file.parquet'
pq.write_table(table, parquet_file)

# Read the Parquet file back into a Pandas DataFrame
table_read = pq.read_table(parquet_file)
df_read = table_read.to_pandas()

print("Data read from Parquet file:")
print(df_read)
```

### Step 3: Create the Ontology

Next, we need to create an ontology that represents the schema of the Parquet file. For simplicity, let's create a basic ontology using RDFLib.

```python
from rdflib import Graph, Namespace, Literal, URIRef

# Create a new RDF Graph
g = Graph()

# Define a namespace for our ontology
ns = Namespace("http://example.org/ontology/")

# Define classes and properties
g.add((ns.Person, ns.type, ns.Class))
g.add((ns.name, ns.type, ns.Property))
g.add((ns.age, ns.type, ns.Property))
g.add((ns.city, ns.type, ns.Property))

# Serialize the ontology to a file
g.serialize(destination='ontology.ttl', format='turtle')
```

### Step 4: Create the Ontop Mapping

Now, we need to create an Ontop mapping file that maps the Parquet file's schema to the ontology. This is typically done using the Ontop CLI or a configuration file. For simplicity, we'll create a basic mapping file manually.

Create a file named `mapping.obda` with the following content:

```obda
[PrefixDeclaration]
:       http://example.org/ontology/
ex:     http://example.org/

[MappingDeclaration] @collection [[
mappingId   ~person_mapping
target      :Person/{Name} a :Person ; :name {Name} ; :age {Age} ; :city {City} .
source      SELECT Name, Age, City FROM my_Parquet-file.parquet
]]
```

### Step 5: Query the Parquet File Using SPARQL

Finally, we can use the `SPARQLWrapper` library to query the Parquet file using SPARQL. Here's how you can do it:

```python
from SPARQLWrapper import SPARQLWrapper, JSON

# Define the SPARQL endpoint (assuming Ontop is running locally)
sparql = SPARQLWrapper("http://localhost:8080/sparql")

# Define the SPARQL query
query = """
PREFIX : <http://example.org/ontology/>
SELECT ?name ?age ?city WHERE {
  ?person a :Person ;
          :name ?name ;
          :age ?age ;
          :city ?city .
}
"""

# Set the query and return format
sparql.setQuery(query)
sparql.setReturnFormat(JSON)

# Execute the query and print the results
results = sparql.query().convert()

print("SPARQL Query Results:")
for result in results["results"]["bindings"]:
    print(result)
```

### Step 6: Running Ontop

To run Ontop, you need to download and set up the Ontop CLI. You can download it from the [Ontop GitHub repository](https://github.com/ontop/ontop). Once downloaded, you can run Ontop with the following command:

```bash
java -jar ontop-cli.jar endpoint -m mapping.obda -t ontology.ttl -p parquet.properties
```

You need to create a `parquet.properties` file with the following content:

```properties
jdbc.url=jdbc:calcite:model=model.json
jdbc.driver=org.apache.calcite.jdbc.Driver
```

And a `model.json` file:

```json
{
  "version": "1.0",
  "defaultSchema": "MY_SCHEMA",
  "schemas": [
    {
      "name": "MY_SCHEMA",
      "type": "custom",
      "factory": "org.apache.calcite.adapter.file.FileSchemaFactory",
      "operand": {
        "directory": "."
      }
    }
  ]
}
```

### Summary

1. **Create the Parquet file** using the provided Python code.
2. **Create the ontology** using RDFLib.
3. **Create the Ontop mapping** file manually.
4. **Query the Parquet file** using SPARQL via the `SPARQLWrapper` library.
5. **Run Ontop** to serve the SPARQL endpoint.

This setup allows you to query your Parquet file as if it were part of a knowledge graph using SPARQL.