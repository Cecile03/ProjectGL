import type { UserRole } from '@/services/types';

export const formatUserRoles = (roles?: UserRole[]) => {
  if (!roles) return 'Non défini';

  const formattedRoles = roles.map((role) => {
    switch (role) {
      case 'SS':
        return 'Equipe des encadrants';
      case 'OL':
        return "Responsable d'option";
      case 'OS':
        return 'Etudiant';
      case 'PL':
        return 'Référent UE';
      case 'TC':
        return 'Coach Technique';
    }
  });

  return formattedRoles.join(', ');
};

export const formatDateToDDMMYY = (date: Date): string => {
  const day = String(date.getDate()).padStart(2, '0');
  const month = String(date.getMonth() + 1).padStart(2, '0'); // Les mois sont basés sur zéro, donc ajoutez 1
  const year = String(date.getFullYear()); // Obtenir les deux derniers chiffres de l'année

  return `${day}/${month}/${year}`;
};
