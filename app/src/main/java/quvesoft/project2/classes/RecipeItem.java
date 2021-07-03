package quvesoft.project2.classes;

import java.io.Serializable;

public class RecipeItem implements Serializable{
    private int TYPE;
    private int RecipeImg;
    private String Title;
    private String[] Ingredients;
    private String Explain;
    private int Date;
    private String[] Tags;

    public RecipeItem(int TYPE, int recipeImg, String title, String[] ingredients, String explain, int date, String[] tags) {
        this.TYPE = TYPE;
        RecipeImg = recipeImg;
        Title = title;
        Ingredients = ingredients;
        Explain = explain;
        Date = date;
        Tags = tags;
    }

    public int getTYPE() {
        return TYPE;
    }

    public int getRecipeImg() {
        return RecipeImg;
    }

    public String getTitle() {
        return Title;
    }

    public String[] getIngredients() {
        return Ingredients;
    }

    public String getExplain() {
        return Explain;
    }

    public int getDate() {
        return Date;
    }

    public String[] getTags() {
        return Tags;
    }
}
