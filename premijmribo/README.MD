# Informe de Desenvolupament

## Disseny de l'Arquitectura
- Hi ha cap disseny, en forma de diagrama (pot ser informal, en paper) de l’arquitectura de la solució?
  - Per les APIs hem fet un petit esquema en paper: [Esquema en Paper](./images/BackDiagram.jpg).
## Especificacions de les APIs
- Hi ha alguna especificació (torno a repetir, pot ser informal) de les APIs principals entre els subsistemes?

  - Es poden consultar els endpoints detallats a la [DOCS](http://localhost:8081/docs#/).

## Tests
- **S’han fet tests?** 
  - En cap moment buscarem cobertures altes, però sí que té sentit tenir comprovades mínimament les classes bàsiques del nostre sistema.
  - **Estat actual:**  
    No hem fet tests unitaris. Els diferents "tests" que hem anat fent han sigut provant els endpoints contra el frontend fent simulacions d'execució.

## Disseny de la Interfície
- **S’ha fet un petit disseny de la interfície?**  
  - Per al disseny inicial, hem creat dissenys a paper:  
    - [Disseny per a mòbil](./images/phone.jpg).
    - [Disseny per a web](./images/web.jpg).
  - També hem creat una **guia d'estil** perquè totes les interfícies siguin consistents i amigables amb els usuaris, centrant-nos en possibles problemes visuals.

## Estructures de Dades
- **Hi ha alguns diagrames sobre les estructures de dades principals de la solució?**

## Qualitat del Codi
- **El codi és llegible?**  
  - Els noms de classes, mètodes i variables tenen sentit i ajuden a la seva comprensió.  
  - Hem intentat que tot el codi estigui:
    - Comentat.
    - Separat en diferents classes i funcionalitats.
    - Evitant arxius llargs.
    - Seguint els **principis SOLID**.

- **Hi ha comentaris per explicar punts foscos del codi?**  
  - Som conscients que el "real-time" de la web i l’app s’ha de fer amb **websockets**, **notificacions** o **triggers**.  
  - Per falta de temps, no hem pogut implementar el patró **Observer**.

## Autoavaluació
- **Heu estat crítics amb la solució?**  
  - Sí, som conscients de les dreceres preses i de les mancances de la solució.  
  - Tot i ser acceptables pel context, en un entorn real serien completament inadmissibles.  
  - Degut a la rapidesa de la programació, hem prioritzat treure una demo funcional per sobre de fer-la òptima.
