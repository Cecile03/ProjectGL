//Conversion des données du tableau en format CSV
export const convertToCSV = (data: any[]) => {
    let csv = 'id,name,gender\n'; // Ajout du header
    data.forEach((row) => {
        csv += `${row.id},${row.name},${row.gender}\n`; // Implémentation du format pour chaque ligne
    });
    return csv;
};

//Préparation au téléchargement du fichier CSV
export const downloadCSV = (csv: string, filename: string) => {
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.setAttribute('href', url);
    link.setAttribute('download', filename);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
};