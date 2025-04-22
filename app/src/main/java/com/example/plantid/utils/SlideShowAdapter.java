package com.example.plantid.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Outline;
import android.net.Uri;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantid.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class SlideShowAdapter extends RecyclerView.Adapter<SlideShowAdapter.ImageViewHolder> {
    private final List<Uri> imageUris;
    private final Context context;

    public SlideShowAdapter(Context context, List<Uri> imageUris) {
        this.context = context;
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ShapeableImageView imageView = new ShapeableImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        float radius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16,
                context.getResources().getDisplayMetrics()
        );


        imageView.setShapeAppearanceModel(
                imageView.getShapeAppearanceModel()
                        .toBuilder()
                        .setAllCorners(CornerFamily.ROUNDED, radius)
                        .build()
        );

        return new ImageViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri uri = imageUris.get(position);

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap != null) {
                holder.imageView.setImageBitmap(bitmap);
            } else {
                throw new FileNotFoundException();
            }
        } catch (Exception e) {
            // Si l’image ne peut pas être lue, afficher un placeholder
            holder.imageView.setImageResource(R.drawable.add_photo); // à toi de le mettre dans /res/drawable/
        }
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
}

