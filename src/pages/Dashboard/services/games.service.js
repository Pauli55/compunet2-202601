import axiosClient from "../../../lib/axios/axiosClient";

export async function getAllGames() {
    try {
        const response = await axiosClient.get('/games');
        console.log('Games fetched successfully:', response.data);
        return response.data;
    } catch (error) {
        console.error('Error fetching games:', error);
        throw error;
    }
}