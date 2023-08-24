

Decription du projet -> TIM
Requirements -> TIM
Méthodologie de travail -> TIM

Mockups -> ANTHONY

Landing page -> JAROD


## Processus de développement

### Architecture 

Application mo


## Fonctionnalité 

### Application mobile

Techno : Android / Kotlin

Se connecter / S'inscrire
Gestion auth
Scanner un ticket de caisse (Coop, Migros, Aldi, Lidl)

Afficher statistique simple (Contenu du ticket sous format numérique)
Statistique par catégorie
Statistique prix par produit
Mise en forme des donnée du ticket et envoie au backend
Input utilisateur si produit non reconnu



Optionnel :
Upload/ Scanner un PDF ou image (Opt)
Caching local des produits déjà acheté.
Gestion des actions, et statistique


## Backend

Techno : NodeJS / Express / Swagger 

Connexion/Interface avec la BDD
JTW Auth (AWS)
génération envoie des données statistiques
algorithme d'indexation entre le nom sur le ticket et les nom des produits
gestion d'erreur d'indexation
gestion des erreur lors de récéption de ticket  et génération d'une réponse appropié

Automatiser le scrapping hebdomadaire (Lambda en discussion)
Finaliser scrapping pour Aldi

Définir standard pour les route de l'api

Script pour peupler la base de donnée depuis les donnée scrapés


## Base de donnée

NoSQL Document database
AWS DocumentDB (MongoDB Compatible)

Document pour les produits scrappé
Document pour les users
Document pour les ticket
Structure à définir 


