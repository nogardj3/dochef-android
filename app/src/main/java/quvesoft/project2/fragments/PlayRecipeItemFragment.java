package quvesoft.project2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.flexbox.FlexboxLayout;

import butterknife.ButterKnife;
import quvesoft.project2.R;
import quvesoft.project2.classes.RecipeItem;

public class PlayRecipeItemFragment extends Fragment {
    @BindView(R.id.playrecipe_item_img)
    AppCompatImageView recipeImage;
    @BindView(R.id.playrecipe_item_ingredients)
    FlexboxLayout recipeIngredients;
    @BindView(R.id.playrecipe_item_explain)
    AppCompatTextView recipeExplain;

    private RecipeItem recipeItem;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_playrecipe_item, container, false);
        ButterKnife.bind(this, view);

        recipeItem = (RecipeItem) getArguments().getSerializable("item");

        Glide.with(this)
                .load(recipeItem.getRecipeImg())
                .apply(RequestOptions.centerCropTransform())
                .into(recipeImage);

        recipeIngredients.removeAllViews();
        for (int i = 0; i < recipeItem.getIngredients().length; i++) {
            LinearLayout motherview = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.v_ingredient, null);
            ((AppCompatTextView) motherview.findViewById(R.id.v_ingredient_product)).setText(recipeItem.getIngredients()[i]);
            ((AppCompatTextView) motherview.findViewById(R.id.v_ingredient_quantity)).setText("0");
            recipeIngredients.addView(motherview);
        }

        recipeExplain.setText(recipeItem.getExplain());

        return view;
    }
}
