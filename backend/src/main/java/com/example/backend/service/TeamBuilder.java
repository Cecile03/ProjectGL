package com.example.backend.service;

import com.example.backend.model.Criteria;
import com.example.backend.model.User;

import java.util.*;

/**
 * Classe utilitaire pour la construction des équipes.
 */
public class TeamBuilder {

    private TeamBuilder() {
        // Private constructor to prevent instantiation
    }

    /**
     * Construit les équipes à partir des utilisateurs et des critères donnés.
     *
     * @param users    La liste des utilisateurs.
     * @param criteria Les critères de construction des équipes.
     * @return La liste des équipes construites.
     */
    public static List<List<User>> buildTeams(List<User> users, Criteria criteria) {
        List<User> femaleBachelorUsers = new ArrayList<>();
        List<User> maleBachelorUsers = new ArrayList<>();
        List<User> femaleNonBachelorUsers = new ArrayList<>();
        List<User> maleNonBachelorUsers = new ArrayList<>();

        // Séparation des étudiants par genre et par statut de bachelier
        separateUsersByGenderAndBachelor(users, femaleBachelorUsers, maleBachelorUsers, femaleNonBachelorUsers, maleNonBachelorUsers);

        criteria.setNumberOfBachelor((int) Math.ceil((double) (femaleBachelorUsers.size() + maleBachelorUsers.size()) / criteria.getNumberOfTeams()));

        // Tri des listes par ordre de mérite si nécessaire
        sortUsersByGrades(femaleBachelorUsers);
        sortUsersByGrades(maleBachelorUsers);
        sortUsersByGrades(femaleNonBachelorUsers);
        sortUsersByGrades(maleNonBachelorUsers);

        List<Integer> distribution = distributeEquitably(users.size(), criteria.getNumberOfTeams());

        int totalFemale = femaleBachelorUsers.size()+femaleNonBachelorUsers.size();

        List<Integer> girlsDistribution = distributeGirls(totalFemale, distribution, criteria.getNumberOfGirls());

        Map<String, List<Integer>> distributions = distributeBachelors(femaleBachelorUsers.size(), maleBachelorUsers.size(), girlsDistribution, distribution);

        return assignUsersToTeams(distributions, femaleBachelorUsers, maleBachelorUsers, femaleNonBachelorUsers, maleNonBachelorUsers);
    }

    /**
     * Sépare les utilisateurs par genre et par statut de bachelier.
     *
     * @param users La liste des utilisateurs.
     * @param femaleBachelorUsers La liste des utilisatrices bachelières
     * @param maleBachelorUsers La liste des utilisateurs bacheliers
     * @param femaleNonBachelorUsers La liste des utilisatrices non bachelières
     * @param maleNonBachelorUsers La liste des utilisateurs non bacheliers
     */
    private static void separateUsersByGenderAndBachelor(List<User> users, List<User> femaleBachelorUsers, List<User> maleBachelorUsers, List<User> femaleNonBachelorUsers, List<User> maleNonBachelorUsers) {
        for (User user : users) {
            if (user.getGender().equals("female")) {
                if (user.isBachelor()) {
                    femaleBachelorUsers.add(user);
                } else {
                    femaleNonBachelorUsers.add(user);
                }
            } else {
                if (user.isBachelor()) {
                    maleBachelorUsers.add(user);
                } else {
                    maleNonBachelorUsers.add(user);
                }
            }
        }
    }

    /**
     * Trie les utilisateurs par ordre de mérite.
     *
     * @param users La liste des utilisateurs
     */
    private static void sortUsersByGrades(List<User> users) {
        for(User user : users) {
            if (user.getGradePast() == null){
                throw new IllegalArgumentException("User " + user.getEmail() + " has no grade");
            }
        }
        users.sort(Comparator.comparingDouble(User::getGradePast));
    }

    /**
     * Distribue équitablement les utilisateurs dans les équipes.
     *
     * @param total Le nombre total d'utilisateurs
     * @param size La taille des équipes
     * @return La distribution des utilisateurs
     */
    public static List<Integer> distributeEquitably(int total, int size) {
        List<Integer> distribution = new ArrayList<>();

        int equalShare = total / size;
        int remainder = total % size;

        distributeEqually(distribution, size, equalShare);
        distributeRemainder(distribution, remainder);

        return distribution;
    }

    /**
     * Distribue équitablement les utilisateurs dans les équipes.
     *
     * @param distribution La distribution des utilisateurs
     * @param size La taille des équipes
     * @param equalShare La part équitable
     */
    private static void distributeEqually(List<Integer> distribution, int size, int equalShare) {
        for (int i = 0; i < size; i++) {
            distribution.add(equalShare);
        }
    }

    /**
     * Distribue le reste des utilisateurs dans les équipes.
     *
     * @param distribution La distribution des utilisateurs
     * @param remainder Le reste des utilisateurs
     */
    private static void distributeRemainder(List<Integer> distribution, int remainder) {
        for (int i = 0; i < remainder; i++) {
            distribution.set(i, distribution.get(i) + 1);
        }
    }

    /**
     * Distribue les utilisatrices dans les équipes.
     *
     * @param totalGirls Le nombre total d'utilisatrices
     * @param distribution La distribution des utilisateurs
     * @param girlsPerTeam Le nombre d'utilisatrices par équipe
     * @return La distribution des utilisatrices
     */
    public static List<Integer> distributeGirls(int totalGirls, List<Integer> distribution, int girlsPerTeam) {
        List<Integer> girlsDistribution = new ArrayList<>(distribution);
        if (totalGirls / girlsPerTeam <= distribution.size()) {
            distributeEvenly(girlsDistribution, totalGirls, girlsPerTeam);
        } else {
            adjustDistribution(girlsDistribution, totalGirls, girlsPerTeam);
            Collections.reverse(girlsDistribution);
        }
        return girlsDistribution;
    }

    /**
     * Distribue les utilisatrices de manière équitable.
     *
     * @param girlsDistribution La distribution des utilisatrices
     * @param totalGirls Le nombre total d'utilisatrices
     * @param girlsPerTeam Le nombre d'utilisatrices par équipe
     */
    private static void distributeEvenly(List<Integer> girlsDistribution, int totalGirls, int girlsPerTeam) {
        for (int i = 0; i < girlsDistribution.size(); i++) {
            if (totalGirls >= girlsPerTeam) {
                girlsDistribution.set(i, girlsPerTeam);
                totalGirls -= girlsPerTeam;
            } else {
                girlsDistribution.set(i, totalGirls);
                totalGirls = 0;
            }
        }
    }

    /**
     * Ajuste la distribution des utilisatrices.
     *
     * @param girlsDistribution La distribution des utilisatrices
     * @param totalGirls Le nombre total d'utilisatrices
     * @param girlsPerTeam Le nombre d'utilisatrices par équipe
     */
    private static void adjustDistribution(List<Integer> girlsDistribution, int totalGirls, int girlsPerTeam) {
        for (int i = 0; i < girlsDistribution.size(); i++) {
            int actualCapacity = girlsDistribution.get(i);
            int futureCapacity = calculateFutureCapacity(girlsDistribution, i);
            boolean addGirls = false;
            while (!addGirls) {
                if (futureCapacity >= totalGirls - girlsPerTeam && girlsPerTeam <= actualCapacity) {
                    addGirls = true;
                } else {
                    girlsPerTeam++;
                }
            }
            girlsDistribution.set(i, girlsPerTeam);
            totalGirls -= girlsPerTeam;
        }
    }

    /**
     * Calcule la capacité future.
     *
     * @param girlsDistribution La distribution des utilisatrices
     * @param currentIndex L'index courant
     * @return La capacité future
     */
    private static int calculateFutureCapacity(List<Integer> girlsDistribution, int currentIndex) {
        int futureCapacity = 0;
        for (int j = currentIndex + 1; j < girlsDistribution.size(); j++) {
            futureCapacity += girlsDistribution.get(j);
        }
        return futureCapacity;
    }

    /**
     * Distribue les bacheliers dans les équipes.
     *
     * @param bachelorGirls Le nombre de filles bacheliers
     * @param bachelorBoys Le nombre de garçons bacheliers
     * @param girlsDistribution La distribution des filles
     * @param distributionTeam La distribution des équipes
     * @return La distribution des bacheliers
     */
    private static Map<String, List<Integer>> distributeBachelors(int bachelorGirls, int bachelorBoys, List<Integer> girlsDistribution, List<Integer> distributionTeam) {
        List<Integer> bachelorGirlDistribution = new ArrayList<>(Collections.nCopies(girlsDistribution.size(), 0));
        List<Integer> nonBachelorGirlDistribution = new ArrayList<>(girlsDistribution);

        placeGirlsInTeams(bachelorGirls, girlsDistribution, bachelorGirlDistribution, nonBachelorGirlDistribution);

        List<Integer> bachelorDistribution = new ArrayList<>(Collections.nCopies(bachelorGirlDistribution.size(), 0));
        List<Integer> bachelorBoyDistribution = new ArrayList<>(Collections.nCopies(girlsDistribution.size(), 0));

        placeBoysInTeams(bachelorBoys, distributionTeam, girlsDistribution, bachelorDistribution, bachelorBoyDistribution);

        List<Integer> nonBachelorBoyDistribution = calculateNonBachelorBoysDistribution(distributionTeam, bachelorBoyDistribution, nonBachelorGirlDistribution, bachelorGirlDistribution);

        return createDistributionMap(bachelorGirlDistribution, nonBachelorGirlDistribution, bachelorBoyDistribution, nonBachelorBoyDistribution, distributionTeam);
    }

    /**
     * Place les filles bacheliers dans les équipes.
     *
     * @param bachelorGirls Le nombre de filles bacheliers
     * @param girlsDistribution La distribution des filles
     * @param bachelorGirlDistribution La distribution des filles bacheliers
     * @param nonBachelorGirlDistribution La distribution des filles non bacheliers
     */
    private static void placeGirlsInTeams(int bachelorGirls, List<Integer> girlsDistribution, List<Integer> bachelorGirlDistribution, List<Integer> nonBachelorGirlDistribution) {
        while (bachelorGirls > 0) {
            for (int i = 0; i < girlsDistribution.size(); i++) {
                if (nonBachelorGirlDistribution.get(i) > 0) {
                    bachelorGirlDistribution.set(i, bachelorGirlDistribution.get(i) + 1);
                    bachelorGirls--;
                    nonBachelorGirlDistribution.set(i, nonBachelorGirlDistribution.get(i) - 1);
                    if (bachelorGirls == 0) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Place les garçons bacheliers dans les équipes.
     *
     * @param bachelorBoys Le nombre de garçons bacheliers
     * @param distributionTeam La distribution des équipes
     * @param girlsDistribution La distribution des filles
     * @param bachelorDistribution La distribution des bacheliers
     * @param bachelorBoyDistribution La distribution des garçons bacheliers
     */
    private static void placeBoysInTeams(int bachelorBoys, List<Integer> distributionTeam, List<Integer> girlsDistribution, List<Integer> bachelorDistribution, List<Integer> bachelorBoyDistribution) {
        while (bachelorBoys > 0) {
            bachelorBoys = distributeBoys(bachelorBoys, distributionTeam, girlsDistribution, bachelorDistribution, bachelorBoyDistribution);
        }
    }

    /**
     * Distribue les garçons dans les équipes.
     *
     * @param bachelorBoys Le nombre de garçons bacheliers
     * @param distributionTeam La distribution des équipes
     * @param girlsDistribution La distribution des filles
     * @param bachelorDistribution La distribution des bacheliers
     * @param bachelorBoyDistribution La distribution des garçons bacheliers
     * @return Le nombre de garçons restants
     */
    private static int distributeBoys(int bachelorBoys, List<Integer> distributionTeam, List<Integer> girlsDistribution, List<Integer> bachelorDistribution, List<Integer> bachelorBoyDistribution) {
        for (int i = distributionTeam.size() - 1; i >= 0; i--) {
            if (girlsDistribution.get(i) < distributionTeam.get(i)) {
                bachelorBoys = updateDistribution(i, bachelorBoys, bachelorDistribution, bachelorBoyDistribution);
                if (bachelorBoys == 0) {
                    break;
                }
            }
        }
        return bachelorBoys;
    }

    /**
     * Met à jour la distribution des garçons.
     *
     * @param i L'index
     * @param bachelorBoys Le nombre de garçons bacheliers
     * @param bachelorDistribution La distribution des bacheliers
     * @param bachelorBoyDistribution La distribution des garçons bacheliers
     * @return Le nombre de garçons restants
     */
    private static int updateDistribution(int i, int bachelorBoys, List<Integer> bachelorDistribution, List<Integer> bachelorBoyDistribution) {
        if (i == 0 || bachelorDistribution.get(i - 1) <= bachelorDistribution.get(i)) {
            incrementDistribution(i, bachelorDistribution, bachelorBoyDistribution);
            bachelorBoys--;
        }
        return bachelorBoys;
    }

    /**
     * Incrémente la distribution des garçons.
     *
     * @param i L'index
     * @param bachelorDistribution La distribution des bacheliers
     * @param bachelorBoyDistribution La distribution des garçons bacheliers
     */
    private static void incrementDistribution(int i, List<Integer> bachelorDistribution, List<Integer> bachelorBoyDistribution) {
        bachelorBoyDistribution.set(i, bachelorBoyDistribution.get(i) + 1);
        bachelorDistribution.set(i, bachelorDistribution.get(i) + 1);
    }

    /**
     * Calcule la distribution des garçons non bacheliers.
     *
     * @param distributionTeam La distribution des équipes
     * @param bachelorBoyDistribution La distribution des garçons bacheliers
     * @param nonBachelorGirlDistribution La distribution des filles non bacheliers
     * @param bachelorGirlDistribution La distribution des filles bacheliers
     * @return La distribution des garçons non bacheliers
     */
    private static List<Integer> calculateNonBachelorBoysDistribution(List<Integer> distributionTeam, List<Integer> bachelorBoyDistribution, List<Integer> nonBachelorGirlDistribution, List<Integer> bachelorGirlDistribution) {
        List<Integer> nonBachelorBoyDistribution = new ArrayList<>();
        for (int i = 0; i < distributionTeam.size(); i++) {
            int nonBachelorBoys = distributionTeam.get(i) - bachelorBoyDistribution.get(i) - nonBachelorGirlDistribution.get(i) - bachelorGirlDistribution.get(i);
            nonBachelorBoyDistribution.add(nonBachelorBoys);
        }
        return nonBachelorBoyDistribution;
    }

    /**
     * Crée une carte de distribution.
     *
     * @param bachelorGirlDistribution La distribution des filles bacheliers
     * @param nonBachelorGirlDistribution La distribution des filles non bacheliers
     * @param bachelorBoyDistribution La distribution des garçons bacheliers
     * @param nonBachelorBoyDistribution La distribution des garçons non bacheliers
     * @param distributionTeam La distribution des équipes
     * @return La carte de distribution
     */
    private static Map<String, List<Integer>> createDistributionMap(List<Integer> bachelorGirlDistribution, List<Integer> nonBachelorGirlDistribution, List<Integer> bachelorBoyDistribution, List<Integer> nonBachelorBoyDistribution, List<Integer> distributionTeam) {
        Map<String, List<Integer>> result = new HashMap<>();
        result.put("bachelorGirlDistribution", bachelorGirlDistribution);
        result.put("nonBachelorGirlDistribution", nonBachelorGirlDistribution);
        result.put("bachelorBoyDistribution", bachelorBoyDistribution);
        result.put("nonBachelorBoyDistribution", nonBachelorBoyDistribution);
        result.put("distributionTeam", distributionTeam);
        return result;
    }

    /**
     * Affecte les utilisateurs aux équipes.
     *
     * @param distributions La distribution des utilisateurs
     * @param femaleBachelorUsers La liste des utilisatrices bachelières
     * @param maleBachelorUsers La liste des utilisateurs bacheliers
     * @param femaleNonBachelorUsers La liste des utilisatrices non bachelières
     * @param maleNonBachelorUsers La liste des utilisateurs non bacheliers
     * @return La liste des équipes
     */
    public static List<List<User>> assignUsersToTeams(Map<String, List<Integer>> distributions, List<User> femaleBachelorUsers, List<User> maleBachelorUsers, List<User> femaleNonBachelorUsers, List<User> maleNonBachelorUsers) {
        List<Integer> teamSizes= distributions.get("distributionTeam");
        // Initialiser la liste de listes de null dans la première dimension
        List<List<User>> teams = new ArrayList<>(Collections.nCopies(teamSizes.size(), null));

        // Initialiser chaque liste de null dans la deuxième dimension
        for (int i = 0; i < teamSizes.size(); i++) {
            teams.set(i, new ArrayList<>());
        }

        assignUsersToTeam(femaleBachelorUsers, distributions.get("bachelorGirlDistribution"), teams);
        assignUsersToTeam(maleBachelorUsers, distributions.get("bachelorBoyDistribution"), teams);
        assignUsersToTeam(femaleNonBachelorUsers, distributions.get("nonBachelorGirlDistribution"), teams);
        assignUsersToTeam(maleNonBachelorUsers, distributions.get("nonBachelorBoyDistribution"), teams);

        return teams;
    }

    /**
     * Affecte les utilisateurs à une équipe.
     *
     * @param users La liste des utilisateurs
     * @param teamDistribution La distribution des équipes
     * @param teams La liste des équipes
     */
    private static void assignUsersToTeam(List<User> users, List<Integer> teamDistribution, List<List<User>> teams) {
        for (int i = 0; i < teams.size(); i++) {
            while (teamDistribution.get(i) > 0 && !users.isEmpty()) {
                teams.get(i).add(users.remove(0));
                teamDistribution.set(i, teamDistribution.get(i) - 1);
            }
        }
    }

    /**
     * Équilibre les équipes.
     *
     * @param teamsOrganized La liste des équipes
     * @param criteria Les critères de construction des équipes
     * @return La liste des équipes équilibrées
     */
    public static List<List<User>> balanceTeams(List<List<User>> teamsOrganized, Criteria criteria) {
        double[] moyennes = new double[teamsOrganized.size()];

        // Calcul des moyennes de chaque liste
        for (int i = 0; i < teamsOrganized.size(); i++) {
            moyennes[i] = calculerMoyenne(teamsOrganized.get(i));
        }

        double threshold = 0.1;
        int cmpt = 0;

        while (ecartMoyennes(moyennes) > threshold) {
            cmpt += 1;
            int indiceMin = indiceMinMoyenne(moyennes);
            int indiceMax = indiceMaxMoyenne(moyennes);

            // Transfert de l'étudiant
            transfererEtudiant(teamsOrganized.get(indiceMax), teamsOrganized.get(indiceMin));

            // Recalcul des moyennes
            moyennes[indiceMin] = calculerMoyenne(teamsOrganized.get(indiceMin));
            moyennes[indiceMax] = calculerMoyenne(teamsOrganized.get(indiceMax));

            if (cmpt > 1000) {
                threshold += 0.05;
                cmpt = 0;
            }
        }

        criteria.setMinAverageThreshold((double) Math.round(threshold * 100) / 100);

        return teamsOrganized;
    }

    /**
     * Calcule la moyenne des notes des étudiants.
     *
     * @param users La liste des utilisateurs
     * @return La moyenne des notes des étudiants
     */
    public static double calculerMoyenne(List<User> users) {
        double total = 0;
        for(User user : users) {
            total += user.getGradePast();
        }
        return total / users.size();
    }

    /**
     * Calcule l'écart entre les moyennes des équipes.
     *
     * @param moyennes Les moyennes des équipes
     * @return L'écart entre les moyennes des équipes
     */
    public static double ecartMoyennes(double[] moyennes) {
        double min = moyennes[0];
        double max = moyennes[0];
        for (double moyenne : moyennes) {
            if (moyenne < min) min = moyenne;
            if (moyenne > max) max = moyenne;
        }
        return max - min;
    }

    /**
     * Trouve l'indice de la moyenne minimale.
     *
     * @param moyennes Les moyennes des équipes
     * @return L'indice de la moyenne minimale
     */
    public static int indiceMinMoyenne(double[] moyennes) {
        int indiceMin = 0;
        for (int i = 1; i < moyennes.length; i++) {
            if (moyennes[i] < moyennes[indiceMin]) {
                indiceMin = i;
            }
        }
        return indiceMin;
    }

    /**
     * Trouve l'indice de la moyenne maximale.
     *
     * @param moyennes Les moyennes des équipes
     * @return L'indice de la moyenne maximale
     */
    public static int indiceMaxMoyenne(double[] moyennes) {
        int indiceMax = 0;
        for (int i = 1; i < moyennes.length; i++) {
            if (moyennes[i] > moyennes[indiceMax]) {
                indiceMax = i;
            }
        }
        return indiceMax;
    }

    /**
     * Transfère l'étudiant avec la note la plus élevée dans la source vers la destination.
     *
     * @param source La liste source
     * @param destination La liste destination
     */
    public static void transfererEtudiant(List<User> source, List<User> destination) {
        // Trouver l'étudiant avec la note la plus élevée dans la source
        double maxNoteSource = source.get(0).getGradePast();
        int maxIndexSource = 0;
        for (int i = 1; i < source.size(); i++) {
            if (source.get(i).getGradePast() > maxNoteSource) {
                maxNoteSource = source.get(i).getGradePast();
                maxIndexSource = i;
            }
        }

        // Trouver l'étudiant avec la note la plus basse dans la destination
        double minNoteDestination = destination.get(0).getGradePast();
        int minIndexDestination = 0;
        for (int i = 1; i < destination.size(); i++) {
            if (destination.get(i).getGradePast() < minNoteDestination) {
                minNoteDestination = destination.get(i).getGradePast();
                minIndexDestination = i;
            }
        }

        // Échanger les étudiants
        User etudiantMaxSource = source.remove(maxIndexSource);
        User etudiantMinDestination = destination.remove(minIndexDestination);
        destination.add(etudiantMaxSource);
        source.add(etudiantMinDestination);
    }
}
