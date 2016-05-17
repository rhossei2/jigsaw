package com.jigsaw.core.converter;

import com.jigsaw.core.model.JigsawPiece;
import com.jigsaw.core.util.JarUtils;
import org.eclipse.aether.artifact.Artifact;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by RH on 4/9/2016.
 */
public class ArtifactToJigsawPieceConverter implements Converter<Artifact, JigsawPiece> {

    public JigsawPiece convert(Artifact artifact) {
        JigsawPiece jigsawPiece = new JigsawPiece();
        jigsawPiece.setId(JarUtils.generateId(artifact));
        jigsawPiece.setArtifactId(artifact.getArtifactId());
        jigsawPiece.setGroupId(artifact.getGroupId());
        jigsawPiece.setVersion(artifact.getVersion());
        jigsawPiece.setFile(artifact.getFile());
        jigsawPiece.setExtension(artifact.getExtension());

        return jigsawPiece;
    }
}
