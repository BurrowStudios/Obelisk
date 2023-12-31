package org.burrow_studios.obelisk.internal.action.entity.board.tag;

import org.burrow_studios.obelisk.api.action.entity.board.tag.TagModifier;
import org.burrow_studios.obelisk.api.entities.board.Tag;
import org.burrow_studios.obelisk.internal.action.ModifierImpl;
import org.burrow_studios.obelisk.internal.data.board.TagData;
import org.burrow_studios.obelisk.internal.entities.board.TagImpl;
import org.burrow_studios.obelisk.internal.net.http.Route;
import org.jetbrains.annotations.NotNull;

public class TagModifierImpl extends ModifierImpl<Tag, TagImpl, TagData> implements TagModifier {
    public TagModifierImpl(@NotNull TagImpl tag) {
        super(
                tag,
                Route.Board.Tag.EDIT.builder()
                        .withArg(tag.getBoard().getId())
                        .withArg(tag.getId())
                        .compile(),
                new TagData(tag.getId()),
                TagData::new
        );
    }

    @Override
    public @NotNull TagModifier setName(@NotNull String name) {
        this.data.setName(name);
        return this;
    }
}
